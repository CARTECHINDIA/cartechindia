package com.cartechindia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@Table(
        name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "mobileNumber")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid mobile number. It should be 10 digits and start with 6-9"
    )
    @Column(unique = true, nullable = false)
    private String mobileNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable=false)
    private String passwordHash;

    @Column(unique=true, nullable=false)
    private String username;

    private String fullName;
    private String dateOfBirth;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private boolean active;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name="user_id"))
    @Column(name = "role")
    private Set<Role> roles;

    //Automatically set when saving
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
