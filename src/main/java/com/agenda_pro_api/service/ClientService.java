package com.agenda_pro_api.service;

import com.agenda_pro_api.entity.Client;
import com.agenda_pro_api.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;

    public List<Client> listByUser(UUID userId) {
        return repository.findByUserId(userId);
    }

    public Client create(Client client) {
        return repository.save(client);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}