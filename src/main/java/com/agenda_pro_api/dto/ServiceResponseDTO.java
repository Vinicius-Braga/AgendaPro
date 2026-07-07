package com.agenda_pro_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceResponseDTO(
        UUID id,
        String name,
        BigDecimal price,
        Integer durationMinutes,
        LocalDateTime createdAt
) {
}
