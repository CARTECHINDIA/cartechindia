package com.cartechindia.service.impl;

import com.cartechindia.constraints.AppointmentStatus;
import com.cartechindia.dto.request.AppointmentRequestDto;
import com.cartechindia.dto.request.AppointmentRescheduleRequestDto;
import com.cartechindia.dto.response.AppointmentResponseDto;
import com.cartechindia.entity.Appointment;
import com.cartechindia.entity.CarListing;
import com.cartechindia.entity.User;
import com.cartechindia.exception.InvalidRequestException;
import com.cartechindia.exception.ResourceNotFoundException;
import com.cartechindia.repository.AppointmentRepository;
import com.cartechindia.repository.CarListingRepository;
import com.cartechindia.repository.UserRepository;
import com.cartechindia.service.AppointmentService;
import com.cartechindia.util.DistanceCalculator;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final CarListingRepository carListingRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, UserRepository userRepository, CarListingRepository carListingRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.carListingRepository = carListingRepository;
    }

    private AppointmentResponseDto toResponseDto(Appointment appointment) {
        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .carId(appointment.getCarListing().getId())
                .userName(appointment.getUser().getFirstName() + " " + appointment.getUser().getLastName())
                .userEmail(appointment.getUser().getEmail())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .distanceKm(appointment.getDistanceKm())
                .estimatedTravelTimeMin(appointment.getEstimatedTravelTimeMin())
                .carLatitude(appointment.getCarLatitude())
                .carLongitude(appointment.getCarLongitude())
                .build();
    }

    @Override
    public AppointmentResponseDto scheduleAppointment(AppointmentRequestDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        CarListing carListing = carListingRepository.findApprovedCarById(dto.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car Listing not found with id: " + dto.getCarId()));

        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setCarListing(carListing);

        // Set user location
        appointment.setUserLatitude(dto.getUserLatitude());
        appointment.setUserLongitude(dto.getUserLongitude());

        // Set car location from CarListing
        appointment.setCarLatitude(carListing.getLatitude());
        appointment.setCarLongitude(carListing.getLongitude());

        // Set appointment date & time
        appointment.setAppointmentDate(dto.getDate());
        appointment.setAppointmentTime(dto.getTime());

        // Status default
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        // Calculate distance (km) using Haversine formula
        double distanceKm = DistanceCalculator.calculateDistanceKm(
                dto.getUserLatitude(), dto.getUserLongitude(),
                carListing.getLatitude(), carListing.getLongitude()
        );
        appointment.setDistanceKm(distanceKm);

        // Estimate travel time in minutes (avg speed 40 km/h)
        double travelTimeMin = DistanceCalculator.estimateTravelTimeMin(distanceKm, 40);
        appointment.setEstimatedTravelTimeMin(travelTimeMin);

        // Save appointment
        appointmentRepository.save(appointment);

        return toResponseDto(appointment);
    }


    @Override
    public AppointmentResponseDto rescheduleAppointment(Long appointmentId, AppointmentRescheduleRequestDto dto) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new InvalidRequestException("Appointment not found"));

        LocalDateTime newDateTime = LocalDateTime.of(dto.getNewDate(), dto.getNewTime());
        if (newDateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("Cannot reschedule to a past date/time");
        }

        // Update date & time
        appointment.setAppointmentDate(dto.getNewDate());
        appointment.setAppointmentTime(dto.getNewTime());

        // Recalculate distance & travel time
        if (appointment.getUserLatitude() != null && appointment.getUserLongitude() != null &&
                appointment.getCarLatitude() != null && appointment.getCarLongitude() != null) {

            double distanceKm = DistanceCalculator.calculateDistanceKm(
                    appointment.getUserLatitude(),
                    appointment.getUserLongitude(),
                    appointment.getCarLatitude(),
                    appointment.getCarLongitude()
            );
            appointment.setDistanceKm(distanceKm);

            double travelTimeMin = DistanceCalculator.estimateTravelTimeMin(distanceKm, 40); // 40 km/h average
            appointment.setEstimatedTravelTimeMin(travelTimeMin);
        }

        // Update status
        appointment.setStatus(AppointmentStatus.RESCHEDULED);

        appointmentRepository.save(appointment);
        return toResponseDto(appointment);
    }


    @Override
    public AppointmentResponseDto cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new InvalidRequestException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        return toResponseDto(appointment);
    }

    @Override
    public AppointmentResponseDto getAppointmentById(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new InvalidRequestException("Appointment not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !appointment.getUser().getId().equals(user.getId())) {
            throw new InvalidRequestException("Unauthorized access");
        }

        return toResponseDto(appointment);
    }

    @Override
    public AppointmentResponseDto markAppointmentAsCompleted(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new InvalidRequestException("Appointment not found"));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidRequestException("Cannot mark a cancelled appointment as completed");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
        return toResponseDto(appointment);
    }

    @Override
    public Page<AppointmentResponseDto> getUserAppointments(AppointmentStatus status, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        Page<Appointment> appointments = (status != null)
                ? appointmentRepository.findByUserAndStatus(user, status, pageable)
                : appointmentRepository.findByUser(user, pageable);

        return appointments.map(this::toResponseDto);
    }

    @Override
    public Page<AppointmentResponseDto> getAllAppointmentsForAdmin(Long userId, Long carId, LocalDate date, AppointmentStatus status, Pageable pageable) {
        Specification<Appointment> spec = Specification.where(null);

        if (userId != null) spec = spec.and((r, q, cb) -> cb.equal(r.get("user").get("id"), userId));
        if (carId != null) spec = spec.and((r, q, cb) -> cb.equal(r.get("car").get("id"), carId));
        if (date != null) spec = spec.and((r, q, cb) -> cb.equal(r.get("appointmentDate"), date));
        if (status != null) spec = spec.and((r, q, cb) -> cb.equal(r.get("status"), status));

        return appointmentRepository.findAll(spec, pageable).map(this::toResponseDto);
    }
}
