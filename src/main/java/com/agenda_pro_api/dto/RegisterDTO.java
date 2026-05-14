package com.agenda_pro_api.dto;

public record RegisterDTO(
        String name,
        String email,
        String password
) {
}