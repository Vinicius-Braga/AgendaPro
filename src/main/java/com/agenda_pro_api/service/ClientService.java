package com.agenda_pro_api.service;

import com.agenda_pro_api.dto.ClientResponseDTO;
import com.agenda_pro_api.dto.CreateClientRequestDTO;
import com.agenda_pro_api.entity.Client;
import com.agenda_pro_api.entity.User;
import com.agenda_pro_api.exception.ResourceNotFoundException;
import com.agenda_pro_api.mapper.ClientMapper;
import com.agenda_pro_api.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;

    public List<ClientResponseDTO> listByUser(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public ClientResponseDTO create(CreateClientRequestDTO dto, User owner) {
        Client client = mapper.toEntity(dto, owner);
        Client saved = repository.save(client);
        return mapper.toResponse(saved);
    }

    public void delete(UUID id, UUID userId) {
        // Busca pelo par (id, userId): garante que um usuário só apague os
        // PRÓPRIOS clientes, mesmo sabendo o id de um cliente de outro usuário.
        Client client = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        repository.delete(client);
    }
}
