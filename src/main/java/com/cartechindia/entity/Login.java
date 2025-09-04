package com.cartechindia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Login{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // link to user (optional: you can use @ManyToOne if you want relation)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "success")
    private boolean success;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;
}

