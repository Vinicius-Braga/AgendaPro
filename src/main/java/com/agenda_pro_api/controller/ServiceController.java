package com.agenda_pro_api.controller;

import com.agenda_pro_api.entity.ServiceEntity;
import com.agenda_pro_api.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService service;

    @GetMapping
    public List<ServiceEntity> list(@RequestParam UUID userId) {
        return service.listByUser(userId);
    }

    @PostMapping
    public ServiceEntity create(@RequestBody ServiceEntity serviceEntity) {
        return service.create(serviceEntity);
    }
}