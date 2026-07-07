package com.agenda_pro_api.dto;

import com.agenda_pro_api.entity.AppointmentStatus;
import com.agenda_pro_api.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// client e service usam seus próprios ResponseDTOs (não a entity JPA) — é
// isso que impede qualquer dado sensível do User (ex.: hash de senha) de
// vazar por aninhamento, sem depender só de @JsonIgnore como rede de segurança.
public record AppointmentResponseDTO(
        UUID id,
        ClientResponseDTO client,
        ServiceResponseDTO service,
        LocalDateTime dateTime,
        AppointmentStatus status,
        PaymentStatus paymentStatus,
        BigDecimal price,
        String notes,
        LocalDateTime createdAt
) {
}
