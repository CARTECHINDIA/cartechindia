package com.cartechindia.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRescheduleRequestDto {

    @NotNull(message = "New appointment date is required")
    @Future(message = "Appointment date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(example = "2025-10-25")
    private LocalDate newDate;

    @NotNull(message = "New appointment time is required")
    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(example = "15:30:00")
    private LocalTime newTime;
}
