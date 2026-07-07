package com.agenda_pro_api.repository;

import com.agenda_pro_api.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRepository extends JpaRepository<ServiceEntity, UUID> {
    List<ServiceEntity> findByUserId(UUID userId);

    // Usado para checar posse antes de vincular um serviço a um agendamento —
    // impede que alguém agende usando o serviço de OUTRO usuário.
    Optional<ServiceEntity> findByIdAndUserId(UUID id, UUID userId);
}