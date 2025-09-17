package com.cartechindia.exception;

// Thrown when username/email/phone + password do not match
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}