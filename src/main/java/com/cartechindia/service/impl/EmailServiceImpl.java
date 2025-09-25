package com.cartechindia.service.impl;

import com.cartechindia.exception.EmailSendException;
import com.cartechindia.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @Override
    public void sendOtp(String email, String otpCode) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = "<div style='font-family: Arial, sans-serif; font-size:14px; color:#333;'><h2 style='color:#2E86C1;'>CarTech OTP Verification</h2><p>Dear User,</p><p>Your <b>OTP</b> is:</p><h1 style='letter-spacing:3px; color:#D35400;'>%s</h1><p>This OTP is valid for <b>5 minutes</b>.</p><p>If you did not request this, please ignore this email.</p><br/><p style='color:#999;'>-- CarTech Team</p></div>".formatted(otpCode);

            helper.setTo(email);
            helper.setFrom("shesheraonarote95@gmail.com"); // must match your Gmail
            helper.setSubject("CarTech OTP Verification");
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            // 🔹 Throw custom exception instead of RuntimeException
            throw new EmailSendException(STR."Failed to send OTP email to: \{email}", e);
        }
    }

}
