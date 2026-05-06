package com.agenda_pro_api.service;

import com.agenda_pro_api.entity.Appointment;
import com.agenda_pro_api.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;

    public Appointment create(Appointment appointment) {

        boolean hasConflict = repository.existsByUserIdAndDateTime(
                appointment.getUser().getId(),
                appointment.getDateTime()
        );

        if (hasConflict) {
            throw new RuntimeException("Horário já ocupado");
        }

        return repository.save(appointment);
    }

    public List<Appointment> getDayAgenda(UUID userId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        return repository.findByUserIdAndDateTimeBetween(userId, start, end);
    }

    public List<Appointment> getPending(UUID userId) {
        return repository.findByUserIdAndPaidFalse(userId);
    }
}