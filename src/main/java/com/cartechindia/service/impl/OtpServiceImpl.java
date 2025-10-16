package com.cartechindia.service.impl;

import com.cartechindia.constraints.UserStatus;
import com.cartechindia.entity.Otp;
import com.cartechindia.entity.User;
import com.cartechindia.exception.InvalidOtpException;
import com.cartechindia.exception.OtpAlreadyUsedException;
import com.cartechindia.exception.OtpExpiredException;
import com.cartechindia.repository.OtpRepository;
import com.cartechindia.repository.UserRepository;
import com.cartechindia.service.OtpService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public OtpServiceImpl(OtpRepository otpRepository, UserRepository userRepository, JavaMailSender mailSender) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }



    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);

        log.info("📧 Email sent to {}", to);
    }


    @Override
    @Transactional
    public String verifyOtp(String email, String otpCode) {
        Otp otp = otpRepository.findByEmailAndOtpCode(email, otpCode)
                .orElseThrow(() -> new InvalidOtpException("Invalid OTP"));

        if (otp.isUsed()) {
            throw new OtpAlreadyUsedException("OTP already used");
        }

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
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

        // If user is NOT a DEALER, approve immediately
        if (user.getRole() == null || !user.getRole().contains("DEALER")) {
            user.setStatus(UserStatus.ACTIVE);
            message = "User verified successfully and account approved.";
        } else {
            // Dealer remains PENDING, admin approval required
            message = "Dealer verified successfully. Your account is pending admin approval.";
        }
        userRepository.save(user);

        return message;
    }

    @Override
    @Transactional
    public String resendOtp(String email) {
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Find existing OTP record for this user/email
        Otp otp = otpRepository.findTopByEmailOrderByExpiryTimeDesc(email)
                .orElseThrow(() -> new RuntimeException("No OTP record found for this user"));


        // Generate a new OTP value
        String newOtpCode = String.format("%06d", new Random().nextInt(1_000_000));

        // Update existing OTP record
        otp.setOtpCode(newOtpCode);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);

        otpRepository.save(otp);

        // Resend via email (and SMS if enabled)
        sendEmail(user.getEmail(), "Your Resend OTP Code", "Your new OTP is: " + newOtpCode);
        // smsService.sendOtp(user.getPhone(), newOtpCode);

        return "New OTP sent successfully to your registered email.";
    }




}
