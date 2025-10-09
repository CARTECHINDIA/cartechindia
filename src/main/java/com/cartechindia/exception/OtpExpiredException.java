package com.cartechindia.exception;

public class OtpExpiredException extends RuntimeException {
    public OtpExpiredException(String message) {
        super(message);
    }
}
