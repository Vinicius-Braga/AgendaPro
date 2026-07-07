package com.agenda_pro_api.controller;

import com.agenda_pro_api.dto.ClientResponseDTO;
import com.agenda_pro_api.dto.CreateClientRequestDTO;
import com.agenda_pro_api.entity.User;
import com.agenda_pro_api.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

    // @AuthenticationPrincipal injeta o User que o JwtFilter colocou no
    // SecurityContext ao validar o token. O cliente do JWT não precisa (e não
    // deve) informar o userId manualmente — ele já está implícito no token.
    @GetMapping
    public List<ClientResponseDTO> list(@AuthenticationPrincipal User user) {
        return service.listByUser(user.getId());
    }

    @PostMapping
    public ResponseEntity<ClientResponseDTO> create(
            @RequestBody @Valid CreateClientRequestDTO dto,
            @AuthenticationPrincipal User user
    ) {
        ClientResponseDTO created = service.create(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        service.delete(id, user.getId());
    }
}
