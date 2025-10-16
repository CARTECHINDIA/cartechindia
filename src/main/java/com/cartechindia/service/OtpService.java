package com.cartechindia.service;

public interface OtpService {

    public String verifyOtp(String email, String otpCode);
    String resendOtp(String email);
}
