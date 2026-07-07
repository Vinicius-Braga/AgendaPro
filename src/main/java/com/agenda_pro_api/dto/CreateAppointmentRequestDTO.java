package com.agenda_pro_api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAppointmentRequestDTO(

        @NotNull(message = "O cliente é obrigatório")
        UUID clientId,

        @NotNull(message = "O serviço é obrigatório")
        UUID serviceId,

        @NotNull(message = "A data e hora são obrigatórias")
        @FutureOrPresent(message = "A data do agendamento não pode estar no passado")
        LocalDateTime dateTime,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser maior que zero")
        BigDecimal price,

        String notes
) {
}
