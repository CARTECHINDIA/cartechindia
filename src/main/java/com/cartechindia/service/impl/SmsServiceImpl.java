package com.cartechindia.service.impl;

import com.cartechindia.exception.SmsSendException;
import com.cartechindia.service.SmsService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Async
    @Override
    public void sendOtp(String phone, String otpCode) {
        try {
            // TODO: Integrate with actual SMS provider like Twilio or AWS SNS
            log.info("📱 Sending SMS to {} with OTP: {}", phone, otpCode);

            // simulate delay
            Thread.sleep(1000);

            log.info("✅ OTP SMS sent to {}", phone);

        } catch (Exception e) {
            log.error("❌ Failed to send SMS to {}: {}", phone, e.getMessage(), e);
            throw new SmsSendException(STR."Failed to send SMS to \{phone}", e);
        }
    }
}
