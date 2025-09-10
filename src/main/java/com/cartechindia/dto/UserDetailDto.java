package com.cartechindia.dto;

import com.cartechindia.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserDetailDto {

    @Parameter(description = "First name")
    private String firstName;

    @Parameter(description = "Last name")
    private String lastName;

    @Parameter(description = "Mobile number", required = true)
    private String phone;

    @Parameter(description = "Email address", required = true)
    private String email;

    @Parameter(description = "Password", required = true)
    private String password;

    @Parameter(description = "Retype password for confirmation")
    private String retypePassword;

    @Parameter(description = "City")
    private String city;

    @Parameter(description = "Area")
    private String area;

    @Parameter(description = "Address")
    private String address;

    @Parameter(description = "Username", required = true)
    private String username;

    @Parameter(description = "Date of birth (yyyy-MM-dd)")
    private LocalDate dob;

    @Parameter(description = "Roles assigned to the user (type comma-separated values, e.g., ADMIN, DEALER)")
    private String role;

    @Parameter(
            description = "Optional KYC document if type is DEALER",
            required = false,
            schema = @Schema(type = "string", format = "binary")
    )
    private MultipartFile document;
}
