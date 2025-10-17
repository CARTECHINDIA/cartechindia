package com.cartechindia.dto.response;

import com.cartechindia.constraints.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class AppointmentResponseDto {
    private Long id;
    private Long carId;
    private String userName;
    private String userEmail;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentStatus status;
    private Double distanceKm;
    private Double estimatedTravelTimeMin;
    private Double carLatitude;
    private Double carLongitude;
}
