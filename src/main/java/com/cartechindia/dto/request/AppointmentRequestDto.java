package com.cartechindia.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequestDto {

    @NotNull(message = "Car ID is required")
    private Long carId;

    @NotNull(message = "Appointment date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(type = "string", example = "2025-10-20")
    private LocalDate date;

    @NotNull(message = "Appointment time is required")
    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", example = "10:30:00")
    private LocalTime time;

    @NotNull(message = "User latitude is required")
    @Schema(example = "18.5204")
    private Double userLatitude;

    @NotNull(message = "User longitude is required")
    @Schema(example = "73.8567")
    private Double userLongitude;
}
