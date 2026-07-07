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
