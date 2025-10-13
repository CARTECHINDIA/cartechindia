package com.cartechindia.entity;

import com.cartechindia.constraints.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(
        name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "phone")
        }
)
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^\\d{10}$",
            message = "Invalid mobile number. It should be exactly 10 digits"
    )
    @Column(unique = true, nullable = false)
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable=false)
    private String password;
    private String city;
    private String area;
    private String address;

    @Column(unique=true, nullable=false)
    private String username;

    private LocalDate dob;

    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.PENDING;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name="user_id"))
    @Column(name = "role")
    private Set<String> role;

    // Added field for document (file path or name)
    private String document;

    @PrePersist
    public void onCreate() {
        this.setCreatedDateTime(LocalDateTime.now());
        this.setUpdatedDateTime(LocalDateTime.now());
    }

    @PreUpdate
    public void onUpdate() {
        this.setUpdatedDateTime(LocalDateTime.now());
    }

}
