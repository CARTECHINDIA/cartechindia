package com.cartechindia.exception;

// For invalid role (ADMIN, DEALER, etc.)
public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String message) {
        super(message);
    }
}