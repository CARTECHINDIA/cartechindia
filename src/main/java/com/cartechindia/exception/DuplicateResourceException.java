package com.cartechindia.exception;

// For duplicate values like email/phone
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}