package com.agenda_pro_api.controller;

import com.agenda_pro_api.dto.CreateServiceRequestDTO;
import com.agenda_pro_api.dto.ServiceResponseDTO;
import com.agenda_pro_api.entity.User;
import com.agenda_pro_api.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService service;

    @GetMapping
    public List<ServiceResponseDTO> list(@AuthenticationPrincipal User user) {
        return service.listByUser(user.getId());
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDTO> create(
            @RequestBody @Valid CreateServiceRequestDTO dto,
            @AuthenticationPrincipal User user
    ) {
        ServiceResponseDTO created = service.create(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ServiceResponseDTO update(
            @PathVariable UUID id,
            @RequestBody @Valid CreateServiceRequestDTO dto,
            @AuthenticationPrincipal User user
    ) {
        return service.update(id, dto, user.getId());
    }
}
