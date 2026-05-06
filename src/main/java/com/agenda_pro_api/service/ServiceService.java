package com.agenda_pro_api.service;

import com.agenda_pro_api.entity.ServiceEntity;
import com.agenda_pro_api.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository repository;

    public List<ServiceEntity> listByUser(UUID userId) {
        return repository.findByUserId(userId);
    }

    public ServiceEntity create(ServiceEntity service) {
        return repository.save(service);
    }
}