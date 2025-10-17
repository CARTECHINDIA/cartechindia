package com.cartechindia.repository;

import com.cartechindia.constraints.AppointmentStatus;
import com.cartechindia.entity.Appointment;
import com.cartechindia.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

    Page<Appointment> findByUser(User user, Pageable pageable);
    Page<Appointment> findByUserAndStatus(User user, AppointmentStatus status, Pageable pageable);
}
