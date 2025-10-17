package com.cartechindia.entity;

import com.cartechindia.constraints.AppointmentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointment")
@Data
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Appointment owner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // existing User entity

    // Car being booked
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_listing_id")
    private CarListing carListing; // existing CarListing entity

    // Scheduled date & time
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;

    // Snapshot of user's location when scheduling
    private Double userLatitude;
    private Double userLongitude;

    // Snapshot of car's location (for distance calculation)
    private Double carLatitude;
    private Double carLongitude;

    // Computed distance and estimated travel time (optional)
    private Double distanceKm; // distance in kilometers
    private Double estimatedTravelTimeMin; // estimated travel time in minutes

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    // Optional: track when appointment was updated
    private LocalDateTime updatedAt;
}
