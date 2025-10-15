package com.cartechindia.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRescheduleRequestDto {

    @NotNull(message = "New appointment date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(
            type = "string",
            example = "2025-10-20",
            description = "New appointment date in yyyy-MM-dd format"
    )
    private LocalDate newDate;

    @NotNull(message = "New appointment time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(
            type = "string",
            example = "15:30:00",
            description = "New appointment time in HH:mm:ss format (must be in the future)"
    )
    private LocalTime newTime;

    @Schema(
            description = "Optional reason for rescheduling",
            example = "Buyer requested a later time"
    )
    private String reason;
}
