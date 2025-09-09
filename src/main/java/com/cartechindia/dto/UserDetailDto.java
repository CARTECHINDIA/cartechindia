package com.cartechindia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserDetailDto {

    @Parameter(description = "Mobile number", required = true)
    private String mobileNumber;

    @Parameter(description = "Email address", required = true)
    private String email;

    @Parameter(description = "Password", required = true)
    private String passwordHash;

    @Parameter(description = "Username", required = true)
    private String username;

    @Parameter(description = "Full name")
    private String fullName;

    @Parameter(description = "Date of birth (yyyy-MM-dd)")
    private String dateOfBirth;

    @Parameter(description = "Type of user: BUYER, SELLER, DEALER")
    private String type;

    @Parameter(
            description = "Optional KYC document if type is DEALER",
            required = false,
            schema = @Schema(type = "string", format = "binary")
    )
    private MultipartFile kycDocument;
}
