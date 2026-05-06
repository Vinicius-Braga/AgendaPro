package com.agenda_pro_api.controller;

import com.agenda_pro_api.entity.Appointment;
import com.agenda_pro_api.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @PostMapping
    public Appointment create(@RequestBody Appointment appointment) {
        return service.create(appointment);
    }

    @GetMapping("/day")
    public List<Appointment> getDay(
            @RequestParam UUID userId,
            @RequestParam String date
    ) {
        return service.getDayAgenda(userId, LocalDate.parse(date));
    }

    @GetMapping("/pending")
    public List<Appointment> getPending(@RequestParam UUID userId) {
        return service.getPending(userId);
    }
}