package com.cartechindia.exception;

// For OTP issues
public class OtpException extends RuntimeException {
    public OtpException(String message) {
        super(message);
    }
}