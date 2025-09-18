package com.cartechindia.service.impl;

import com.cartechindia.entity.Otp;
import com.cartechindia.entity.User;
import com.cartechindia.exception.OtpAlreadyUsedException;
import com.cartechindia.exception.OtpExpiredException;
import com.cartechindia.repository.OtpRepository;
import com.cartechindia.repository.UserRepository;
import com.cartechindia.service.EmailService;
import com.cartechindia.service.OtpService;
import com.cartechindia.service.SmsService;
import jakarta.transaction.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
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
        System.out.println("📧 Email sent to " + to);
    }

    @Override
    @Transactional
    public String verifyOtp(String email, String otpCode) {
        Otp otp = otpRepository.findByEmailAndOtpCode(email, otpCode)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (otp.isUsed()) {
            throw new OtpAlreadyUsedException("OTP already used");
        }

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP expired");
        }

        // mark OTP as used
        otp.setUsed(true);
        otpRepository.save(otp);

        // activate user
        User user = otp.getUser();
        user.setActive(true);
        userRepository.save(user);

        return "User verified successfully!";
    }
}
