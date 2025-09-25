package com.cartechindia.entity;

public enum UserStatus {
    PENDING,   // When the user just registered, waiting for approval/verification
    APPROVED,  // When the user is approved and active
    REJECTED,  // When the user is rejected
    BLOCKED,   // When the user is blocked by admin
    INACTIVE
}
