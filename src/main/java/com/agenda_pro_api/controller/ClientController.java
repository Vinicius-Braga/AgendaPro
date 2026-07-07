package com.agenda_pro_api.controller;

import com.agenda_pro_api.entity.Client;
import com.agenda_pro_api.entity.User;
import com.agenda_pro_api.service.ClientService;
import lombok.RequiredArgsConstructor;
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
    public List<Client> list(@AuthenticationPrincipal User user) {
        return service.listByUser(user.getId());
    }

    @PostMapping
    public Client create(
            @RequestBody Client client,
            @AuthenticationPrincipal User user
    ) {
        // O dono do cliente é SEMPRE o usuário autenticado, nunca um valor
        // vindo do corpo da requisição — isso impediria um usuário de criar
        // registros em nome de outro.
        client.setUser(user);
        return service.create(client);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        service.delete(id, user.getId());
    }
}
