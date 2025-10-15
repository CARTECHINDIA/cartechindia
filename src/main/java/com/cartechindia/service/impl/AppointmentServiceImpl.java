package com.cartechindia.service.impl;

import com.cartechindia.constraints.AppointmentStatus;
import com.cartechindia.dto.request.AppointmentRequestDto;
import com.cartechindia.dto.request.AppointmentRescheduleRequestDto;
import com.cartechindia.dto.response.AppointmentResponseDto;
import com.cartechindia.entity.Appointment;
import com.cartechindia.entity.CarListing;
import com.cartechindia.entity.CarMasterData;
import com.cartechindia.entity.User;
import com.cartechindia.exception.InvalidRequestException;
import com.cartechindia.exception.ResourceNotFoundException;
import com.cartechindia.repository.AppointmentRepository;
import com.cartechindia.repository.CarListingRepository;
import com.cartechindia.repository.CarMasterDataRepository;
import com.cartechindia.repository.UserRepository;
import com.cartechindia.service.AppointmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import com.cartechindia.security.SecurityUtil;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CarListingRepository carListingRepository;
    private final UserRepository userRepository;
    private final GoogleMapsClient googleMapsClient;
    private final CarMasterDataRepository carMasterDataRepository;
    private final SecurityUtil securityUtil;

    @Override
    public AppointmentResponseDto schedule(AppointmentRequestDto dto) {
        User currentUser = securityUtil.getCurrentUser();

        CarListing car = carListingRepository.findById(dto.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        CarMasterData carMasterData = carMasterDataRepository.findById(car.getCarMasterDataId())
                .orElseThrow(()->new ResourceNotFoundException("Car master data not found with id : "+car.getCarMasterDataId()));

        // Call Google API for distance/duration
        GoogleMapsClient.DistanceResult distanceResult = googleMapsClient.getDistanceAndDuration(
                dto.getUserLatitude(),
                dto.getUserLongitude(),
                car.getLatitude(),
                car.getLongitude(),
                "driving"
        );

        Appointment appointment = new Appointment();
        appointment.setUser(currentUser);
        appointment.setCarListing(car);
        appointment.setAppointmentDate(dto.getDate());
        appointment.setAppointmentTime(dto.getTime());
        appointment.setDistanceInMeters(distanceResult.getDistanceInMeters());
        appointment.setDistanceText(distanceResult.getDistanceText());
        appointment.setDurationText(distanceResult.getDurationText());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        appointmentRepository.save(appointment);

        AppointmentResponseDto response = new AppointmentResponseDto();
        response.setId(appointment.getId());
        response.setCarId(car.getId());
        response.setCarModel(carMasterData.getModel());
        response.setDate(appointment.getAppointmentDate());
        response.setTime(appointment.getAppointmentTime());
        response.setStatus(appointment.getStatus().name());
        response.setDistanceText(appointment.getDistanceText());
        response.setDurationText(appointment.getDurationText());
        response.setDistanceInMeters(appointment.getDistanceInMeters());
        response.setUserEmail(currentUser.getEmail()); // optional

        return response;
    }



    @Override
    public AppointmentResponseDto getById(Long id) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        CarMasterData carMasterData = carMasterDataRepository.findById(appt.getCarListing().getCarMasterDataId())
                .orElseThrow(()->new ResourceNotFoundException("Car Master Data Not Found With Id : "+appt.getCarListing().getCarMasterDataId()));


            return getAppointmentResponseDto(appt, appt.getCarListing(), carMasterData);
    }

    private static AppointmentResponseDto getAppointmentResponseDto(Appointment appt, CarListing appt1, CarMasterData carMasterData) {
        AppointmentResponseDto dto = new AppointmentResponseDto();
        dto.setId(appt.getId());
        dto.setCarId(appt1.getId());
        dto.setCarModel(carMasterData.getModel());
        dto.setDate(appt.getAppointmentDate());
        dto.setTime(appt.getAppointmentTime());
        dto.setDistanceInMeters(appt.getDistanceInMeters());
        dto.setDistanceText(appt.getDistanceText());
        dto.setDurationText(appt.getDurationText());
        dto.setStatus(appt.getStatus().name());
        return dto;
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        appt.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appt);
    }


    @Override
    @Transactional
    public AppointmentResponseDto reschedule(Long id, AppointmentRescheduleRequestDto dto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        CarMasterData carMasterData = carMasterDataRepository.findById(appointment.getCarListing().getCarMasterDataId())
                .orElseThrow(()->new ResourceNotFoundException("Car Master Data Not Found With Id : "+appointment.getCarListing().getCarMasterDataId()));

        // === Validation ===
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidRequestException("Cannot reschedule a cancelled appointment");
        }
        // Combine date and time from the DTO
        LocalDateTime newAppointmentDateTime = LocalDateTime.of(
                dto.getNewDate(),
                dto.getNewTime()
        );

        // === Validation ===
        if (newAppointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("Cannot reschedule to a past date/time");
        }

        // === Update and Save ===
        appointment.setAppointmentDate(dto.getNewDate());
        appointment.setAppointmentTime(dto.getNewTime());
        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        appointmentRepository.save(appointment);

        // === Convert to Response DTO ===
        return getAppointmentResponseDto(appointment, carMasterData);
    }

    private static AppointmentResponseDto getAppointmentResponseDto(Appointment appointment, CarMasterData carMasterData) {
        AppointmentResponseDto response = new AppointmentResponseDto();
        response.setId(appointment.getId());
        response.setCarId(appointment.getCarListing().getId());
        response.setCarModel(carMasterData.getModel());
        response.setDate(appointment.getAppointmentDate());
        response.setTime(appointment.getAppointmentTime());
        response.setStatus(appointment.getStatus().name());
        response.setDistanceText(appointment.getDistanceText());
        response.setDurationText(appointment.getDurationText());
        response.setDistanceInMeters(appointment.getDistanceInMeters());
        return response;
    }

}