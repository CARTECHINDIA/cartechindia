package com.cartechindia.service;

import com.cartechindia.dto.request.AppointmentRequestDto;
import com.cartechindia.dto.request.AppointmentRescheduleRequestDto;
import com.cartechindia.dto.response.AppointmentResponseDto;

public interface AppointmentService {
    AppointmentResponseDto schedule(AppointmentRequestDto dto);
    AppointmentResponseDto getById(Long id);
    void cancel(Long id);
    AppointmentResponseDto reschedule(Long id, AppointmentRescheduleRequestDto dto);


}