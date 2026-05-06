package com.agenda_pro_api.controller;

import com.agenda_pro_api.entity.Client;
import com.agenda_pro_api.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

    @GetMapping
    public List<Client> list(@RequestParam UUID userId) {
        return service.listByUser(userId);
    }

    @PostMapping
    public Client create(@RequestBody Client client) {
        return service.create(client);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}