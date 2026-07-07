package com.agenda_pro_api.service;

import com.agenda_pro_api.entity.Client;
import com.agenda_pro_api.exception.ResourceNotFoundException;
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

    public void delete(UUID id, UUID userId) {
        // Busca pelo par (id, userId): garante que um usuário só apague os
        // PRÓPRIOS clientes, mesmo sabendo o id de um cliente de outro usuário.
        Client client = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        repository.delete(client);
    }
}