package com.cartechindia.repository;

import com.cartechindia.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // add custom queries if required
}