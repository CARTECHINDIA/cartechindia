package com.cartechindia.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class UserUpdateRequestDto {

    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String city;
    private String area;
    private String address;
    private String username;
    private LocalDate dob;

    @Schema(description = "Optional KYC document if role is DEALER", type = "string", format = "binary")
    private MultipartFile document;
}
