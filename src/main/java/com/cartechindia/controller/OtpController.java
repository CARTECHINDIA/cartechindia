package com.cartechindia.controller;

import com.cartechindia.service.OtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email,
                                            @RequestParam String otpCode) {
        String result = otpService.verifyOtp(email, otpCode);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestParam String email) {
        String result = otpService.resendOtp(email);
        return ResponseEntity.ok(result);
    }

}

