package com.agenda_pro_api.mapper;

import com.agenda_pro_api.dto.CreateServiceRequestDTO;
import com.agenda_pro_api.dto.ServiceResponseDTO;
import com.agenda_pro_api.entity.ServiceEntity;
import com.agenda_pro_api.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    public ServiceEntity toEntity(CreateServiceRequestDTO dto, User owner) {
        ServiceEntity service = new ServiceEntity();
        service.setName(dto.name());
        service.setPrice(dto.price());
        service.setDurationMinutes(dto.durationMinutes());
        service.setUser(owner);
        return service;
    }

    // Aplica os campos editáveis do DTO numa entity JÁ EXISTENTE — preserva
    // id, user e createdAt, que nunca devem mudar numa edição.
    public void updateEntity(ServiceEntity service, CreateServiceRequestDTO dto) {
        service.setName(dto.name());
        service.setPrice(dto.price());
        service.setDurationMinutes(dto.durationMinutes());
    }

    public ServiceResponseDTO toResponse(ServiceEntity service) {
        return new ServiceResponseDTO(
                service.getId(),
                service.getName(),
                service.getPrice(),
                service.getDurationMinutes(),
                service.getCreatedAt()
        );
    }
}
