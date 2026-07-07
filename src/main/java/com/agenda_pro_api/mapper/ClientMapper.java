package com.agenda_pro_api.mapper;

import com.agenda_pro_api.dto.ClientResponseDTO;
import com.agenda_pro_api.dto.CreateClientRequestDTO;
import com.agenda_pro_api.entity.Client;
import com.agenda_pro_api.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public Client toEntity(CreateClientRequestDTO dto, User owner) {
        Client client = new Client();
        client.setName(dto.name());
        client.setPhone(dto.phone());
        client.setUser(owner);
        return client;
    }

    public ClientResponseDTO toResponse(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getPhone(),
                client.getCreatedAt()
        );
    }
}
