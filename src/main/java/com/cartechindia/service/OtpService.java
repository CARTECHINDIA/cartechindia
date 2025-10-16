package com.cartechindia.service;

import com.cartechindia.entity.Otp;
import com.cartechindia.entity.User;

public interface OtpService {

    public String verifyOtp(String email, String otpCode);
    String resendOtp(String email);
    Otp createOtp(User user);
}
