package com.cartechindia.entity;

import com.cartechindia.constraints.AppointmentStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointment")
@Data
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // assume existing User entity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_listing_id")
    private CarListing carListing;

    private LocalDate appointmentDate;
    private LocalTime appointmentTime;

    // snapshot of the user's location when scheduling
    private Double userLatitude;
    private Double userLongitude;

    // computed values from Distance Matrix API
    private Double distanceInMeters;
    private String distanceText;
    private String durationText;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;
}