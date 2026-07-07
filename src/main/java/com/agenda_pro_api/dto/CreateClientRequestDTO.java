package com.agenda_pro_api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateClientRequestDTO(

        @NotBlank(message = "O nome é obrigatório")
        String name,

        String phone
) {
}
