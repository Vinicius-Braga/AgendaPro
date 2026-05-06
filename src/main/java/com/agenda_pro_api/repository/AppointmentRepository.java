package com.agenda_pro_api.repository;

import com.agenda_pro_api.entity.Appointment;
import com.agenda_pro_api.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByUserId(UUID userId);

    List<Appointment> findByUserIdAndPaidFalse(UUID userId);

    List<Appointment> findByUserIdAndDateTimeBetween(
            UUID userId,
            LocalDateTime start,
            LocalDateTime end
    );

    boolean existsByUserIdAndDateTime(UUID userId, LocalDateTime dateTime);

    List<Appointment> findByUserIdAndStatus(UUID userId, AppointmentStatus status);
}