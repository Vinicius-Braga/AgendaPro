package com.agenda_pro_api.controller;

import com.agenda_pro_api.entity.Appointment;
import com.agenda_pro_api.entity.User;
import com.agenda_pro_api.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @PostMapping
    public Appointment create(
            @RequestBody Appointment appointment,
            @AuthenticationPrincipal User user
    ) {
        appointment.setUser(user);
        return service.create(appointment);
    }

    @GetMapping("/day")
    public List<Appointment> getDay(
            @AuthenticationPrincipal User user,
            @RequestParam String date
    ) {
        return service.getDayAgenda(user.getId(), LocalDate.parse(date));
    }

    @GetMapping("/pending")
    public List<Appointment> getPending(@AuthenticationPrincipal User user) {
        return service.getPending(user.getId());
    }
}
