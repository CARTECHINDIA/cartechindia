package com.cartechindia.controller;

import com.cartechindia.constraints.AppointmentStatus;
import com.cartechindia.dto.request.AppointmentRequestDto;
import com.cartechindia.dto.request.AppointmentRescheduleRequestDto;
import com.cartechindia.dto.response.AppointmentResponseDto;
import com.cartechindia.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @Operation(summary = "Schedule an appointment")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/schedule")
    public ResponseEntity<AppointmentResponseDto> schedule(@RequestBody AppointmentRequestDto dto) {
        return ResponseEntity.ok(service.scheduleAppointment(dto));
    }

    @Operation(summary = "Reschedule an appointment")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<AppointmentResponseDto> reschedule(@PathVariable Long id, @RequestBody AppointmentRescheduleRequestDto dto) {
        return ResponseEntity.ok(service.rescheduleAppointment(id, dto));
    }

    @Operation(summary = "Cancel an appointment")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDto> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelAppointment(id));
    }

    @Operation(summary = "Get appointment by ID")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAppointmentById(id));
    }

    @Operation(summary = "Mark appointment as completed")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEALER')")
    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponseDto> complete(@PathVariable Long id) {
        return ResponseEntity.ok(service.markAppointmentAsCompleted(id));
    }

    @Operation(summary = "Get all appointments of logged-in user")
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Page<AppointmentResponseDto>> getUserAppointments(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getUserAppointments(status, pageable));
    }

    @Operation(summary = "Admin - Get all appointments with sorting/filtering")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Page<AppointmentResponseDto>> getAllForAdmin(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long carId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(service.getAllAppointmentsForAdmin(userId, carId, date, status, pageable));
    }
}
