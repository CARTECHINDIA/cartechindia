package com.cartechindia.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentResponseDto {
    private Long id;
    private Long carId;
    private String carModel;
    private LocalDate date;
    private LocalTime time;
    private Double distanceInMeters;
    private String distanceText;
    private String durationText;
    private String status;
    private String userEmail;
}