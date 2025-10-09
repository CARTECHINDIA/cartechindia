package com.cartechindia.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String otpCode;   // the actual OTP value

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    private boolean used = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // optional: if you want to track channel
    private String email;
    private String phone;
}
