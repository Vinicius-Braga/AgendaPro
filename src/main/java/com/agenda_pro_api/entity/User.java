package com.agenda_pro_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // @JsonIgnore garante que o hash da senha NUNCA é serializado em JSON,
    // não importa por qual endpoint o User (ou uma entidade que o referencia,
    // como Client/ServiceEntity/Appointment) seja devolvido. É uma rede de
    // segurança além do UserMapper — que já constrói UserResponseDTO sem
    // senha — pois Client/Service/Appointment ainda retornam a entidade User
    // aninhada diretamente (dívida técnica registrada para o próximo passo
    // de DTOs desses módulos).
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}