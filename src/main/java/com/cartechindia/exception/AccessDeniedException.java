package com.cartechindia.exception;

// Thrown when user tries to access something they don't have permission for
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
