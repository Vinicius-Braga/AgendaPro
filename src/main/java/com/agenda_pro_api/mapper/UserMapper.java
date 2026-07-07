package com.agenda_pro_api.mapper;

import com.agenda_pro_api.dto.RegisterDTO;
import com.agenda_pro_api.dto.UserResponseDTO;
import com.agenda_pro_api.entity.User;
import org.springframework.stereotype.Component;

/**
 * Centraliza a conversão DTO <-> Entity. Mantém Services e Controllers livres
 * dessa "cola" repetitiva e garante um único lugar para ajustar o mapeamento.
 *
 * Nota: a senha é mapeada em texto puro aqui; a criptografia (BCrypt) é
 * responsabilidade do Service, não do Mapper (o Mapper só transporta dados).
 */
@Component
public class UserMapper {

    public User toEntity(RegisterDTO dto) {
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        return user;
    }

    public UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
