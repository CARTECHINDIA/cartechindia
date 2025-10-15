package com.cartechindia.controller;

import com.cartechindia.dto.request.AppointmentRequestDto;
import com.cartechindia.dto.request.AppointmentRescheduleRequestDto;
import com.cartechindia.dto.response.AppointmentResponseDto;
import com.cartechindia.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Appointment Module", description = "Endpoints to schedule and manage physical car visit appointments")
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    private final AppointmentService appointmentService;

    // === Schedule Appointment ===
    @PostMapping("/schedule")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AppointmentResponseDto> scheduleAppointment(
            @RequestBody @Validated AppointmentRequestDto dto
    ) {
        AppointmentResponseDto response = appointmentService.schedule(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    // === Get Appointment by ID ===
    @Operation(
            summary = "Get appointment details",
            description = "Fetches appointment details using appointment ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Appointment details found",
                            content = @Content(schema = @Schema(implementation = AppointmentResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Appointment not found", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointment(@PathVariable Long id) {
        // optional helper in service (you can add it)
        AppointmentResponseDto response = appointmentService.getById(id);
        return ResponseEntity.ok(response);
    }

    // === Cancel Appointment ===
    @Operation(
            summary = "Cancel an appointment",
            description = "Cancels an existing scheduled appointment.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Appointment cancelled successfully"),
                    @ApiResponse(responseCode = "404", description = "Appointment not found")
            }
    )
    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancel(id);
        return ResponseEntity.ok("Appointment cancelled successfully.");
    }


    // === Reschedule Appointment ===
    @Operation(
            summary = "Reschedule an appointment",
            description = """
            Allows user to change the date/time of an existing appointment.
            New date/time must be in the future and not conflict with existing appointments.
        """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Appointment rescheduled successfully",
                            content = @Content(schema = @Schema(implementation = AppointmentResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid date/time provided", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Appointment not found", content = @Content)
            }
    )
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<AppointmentResponseDto> rescheduleAppointment(
            @PathVariable Long id,
            @RequestBody @Validated AppointmentRescheduleRequestDto dto
    ) {
        AppointmentResponseDto response = appointmentService.reschedule(id, dto);
        return ResponseEntity.ok(response);
    }

}
