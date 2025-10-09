package com.cartechindia.service;

public interface SmsService {

    void sendOtp(String phone, String otpCode);
}
