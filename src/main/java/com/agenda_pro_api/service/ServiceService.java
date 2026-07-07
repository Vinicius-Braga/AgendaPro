package com.agenda_pro_api.service;

import com.agenda_pro_api.dto.CreateServiceRequestDTO;
import com.agenda_pro_api.dto.ServiceResponseDTO;
import com.agenda_pro_api.entity.ServiceEntity;
import com.agenda_pro_api.entity.User;
import com.agenda_pro_api.mapper.ServiceMapper;
import com.agenda_pro_api.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository repository;
    private final ServiceMapper mapper;

    public List<ServiceResponseDTO> listByUser(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public ServiceResponseDTO create(CreateServiceRequestDTO dto, User owner) {
        ServiceEntity service = mapper.toEntity(dto, owner);
        ServiceEntity saved = repository.save(service);
        return mapper.toResponse(saved);
    }
}
