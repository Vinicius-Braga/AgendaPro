package com.agenda_pro_api.controller;

import com.agenda_pro_api.entity.Appointment;
import com.agenda_pro_api.entity.User;
import com.agenda_pro_api.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    // PATCH + sub-rota de ação (em vez de um PUT genérico) porque isto não é
    // uma edição de atributo qualquer — é uma transição de estado do domínio,
    // com regras próprias de quando é permitida. Deixar isso explícito na URL
    // também documenta a operação: fica óbvio, sem precisar ler o corpo do
    // request, que "iniciar" é uma ação e não uma atualização arbitrária.
    @PatchMapping("/{id}/start")
    public Appointment start(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        return service.start(id, user.getId());
    }

    @PatchMapping("/{id}/complete")
    public Appointment complete(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        return service.complete(id, user.getId());
    }

    @PatchMapping("/{id}/cancel")
    public Appointment cancel(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        return service.cancel(id, user.getId());
    }

    @PatchMapping("/{id}/pay")
    public Appointment pay(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        return service.pay(id, user.getId());
    }
}
