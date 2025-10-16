package com.cartechindia.repository;

import com.cartechindia.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    // If you want to find by OTP value
    Optional<Otp> findByEmailAndOtpCode(String email, String otpCode);
    Optional<Otp> findTopByEmailOrderByExpiryTimeDesc(String email);

}

