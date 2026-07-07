package com.agenda_pro_api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientResponseDTO(
        UUID id,
        String name,
        String phone,
        LocalDateTime createdAt
) {
}
