package com.agenda_pro_api.controller;

import com.agenda_pro_api.entity.ServiceEntity;
import com.agenda_pro_api.entity.User;
import com.agenda_pro_api.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService service;

    @GetMapping
    public List<ServiceEntity> list(@AuthenticationPrincipal User user) {
        return service.listByUser(user.getId());
    }

    @PostMapping
    public ServiceEntity create(
            @RequestBody ServiceEntity serviceEntity,
            @AuthenticationPrincipal User user
    ) {
        serviceEntity.setUser(user);
        return service.create(serviceEntity);
    }
}
