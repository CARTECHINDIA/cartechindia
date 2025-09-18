package com.cartechindia.service.impl;

import com.cartechindia.service.SmsService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    @Async
    @Override
    public void sendOtp(String phone, String otpCode) {
        try {
            // TODO: Integrate with actual SMS provider like Twilio or AWS SNS
            System.out.println("📱 Sending SMS to " + phone + " with OTP: " + otpCode);

            // simulate delay
            Thread.sleep(1000);

            System.out.println("✅ OTP SMS sent to " + phone);
        } catch (Exception e) {
            System.err.println("❌ Failed to send SMS to " + phone + ": " + e.getMessage());
        }
    }
}
