package com.cartechindia.service;

import com.cartechindia.constraints.AppointmentStatus;
import com.cartechindia.dto.request.AppointmentRequestDto;
import com.cartechindia.dto.request.AppointmentRescheduleRequestDto;
import com.cartechindia.dto.response.AppointmentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AppointmentService {
    AppointmentResponseDto scheduleAppointment(AppointmentRequestDto dto);
    AppointmentResponseDto rescheduleAppointment(Long appointmentId, AppointmentRescheduleRequestDto dto);
    AppointmentResponseDto cancelAppointment(Long appointmentId);
    AppointmentResponseDto getAppointmentById(Long appointmentId);
    AppointmentResponseDto markAppointmentAsCompleted(Long appointmentId);
    Page<AppointmentResponseDto> getUserAppointments(AppointmentStatus status, Pageable pageable);
    Page<AppointmentResponseDto> getAllAppointmentsForAdmin(Long userId, Long carId, LocalDate date, AppointmentStatus status, Pageable pageable);
}
