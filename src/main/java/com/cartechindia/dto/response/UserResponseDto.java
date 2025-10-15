package com.cartechindia.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String city;
    private String area;
    private String address;
    private String username;
    private LocalDate dob;
    private boolean active;
    private String status;
    private Set<String> role;

    @Schema(description = "Relative path to uploaded KYC document, if any")
    private String document;

    private LocalDateTime createdDateTime;
    private LocalDateTime updatedDateTime;
}
