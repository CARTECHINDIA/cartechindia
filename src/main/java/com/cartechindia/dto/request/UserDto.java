package com.cartechindia.dto.request;

import com.cartechindia.constraints.UserRole;
import com.cartechindia.constraints.UserStatus;
import com.cartechindia.entity.Document;
import com.cartechindia.entity.Location;
import jakarta.persistence.*;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Set;

@Data
public class UserDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String phone;

    private String email;

    private String passwordHash;

    private String username;

    private LocalDate dob;

    private UserStatus status = UserStatus.PENDING;

    private Set<UserRole> roles;

    private Location location;

    private Set<Document> documents;
}
