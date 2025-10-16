package com.cartechindia.service.impl;

import com.cartechindia.constraints.UserStatus;
import com.cartechindia.entity.Otp;
import com.cartechindia.entity.User;
import com.cartechindia.exception.InvalidOtpException;
import com.cartechindia.exception.OtpAlreadyUsedException;
import com.cartechindia.exception.OtpExpiredException;
import com.cartechindia.exception.OtpGenerationException;
import com.cartechindia.repository.OtpRepository;
import com.cartechindia.repository.UserRepository;
import com.cartechindia.service.OtpService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final Random random = new Random();

    public OtpServiceImpl(OtpRepository otpRepository, UserRepository userRepository, JavaMailSender mailSender) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    // =========================
    // Send Email Helper
    // =========================
    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        log.info("📧 Email sent to {}", to);
    }

    // =========================
    // Create OTP
    // =========================
    @Override
    public Otp createOtp(User user) {
        try {
            String otpCode = String.format("%06d", random.nextInt(1_000_000));
            Otp otp = new Otp();
            otp.setOtpCode(otpCode);
            otp.setExpiryTime(Instant.now().plus(30, ChronoUnit.MINUTES)); // 30-min expiry
            otp.setUsed(false);
            otp.setUser(user);
            otp.setEmail(user.getEmail());
            otp.setPhone(user.getPhone());
            return otp;
        } catch (Exception e) {
            throw new OtpGenerationException("Failed to generate OTP: " + e.getMessage());
        }
    }

    // =========================
    // Verify OTP
    // =========================
    @Override
    @Transactional
    public String verifyOtp(String email, String otpCode) {
        Otp otp = otpRepository.findByEmailAndOtpCode(email, otpCode)
                .orElseThrow(() -> new InvalidOtpException("Invalid OTP"));

        if (otp.isUsed()) {
            throw new OtpAlreadyUsedException("OTP already used");
        }

        if (otp.getExpiryTime().isBefore(Instant.now())) {
            throw new OtpExpiredException("OTP expired");
        }

        // Mark OTP as used
        otp.setUsed(true);
        otpRepository.save(otp);

        // Activate user
        User user = otp.getUser();
        if (!user.getRole().contains("DEALER")) {
            user.setActive(true);
        }

        String message;
        if (user.getRole() == null || !user.getRole().contains("DEALER")) {
            user.setStatus(UserStatus.ACTIVE);
            message = "User verified successfully and account approved.";
        } else {
            message = "Dealer verified successfully. Your account is pending admin approval.";
        }

        userRepository.save(user);
        return message;
    }

    // =========================
    // Resend OTP
    // =========================
    @Override
    @Transactional
    public String resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        Otp otp = otpRepository.findTopByEmailOrderByExpiryTimeDesc(email)
                .orElseThrow(() -> new RuntimeException("No OTP record found for this user"));

        // Generate new OTP
        String newOtpCode = String.format("%06d", random.nextInt(1_000_000));

        // Update existing OTP record
        otp.setOtpCode(newOtpCode);
        otp.setExpiryTime(Instant.now().plus(30, ChronoUnit.MINUTES)); // 30-minute expiry
        otp.setUsed(false);

        otpRepository.save(otp);

        sendEmail(user.getEmail(), "Your Resend OTP Code", "Your new OTP is: " + newOtpCode);

        return "New OTP sent successfully to your registered email.";
    }
}
