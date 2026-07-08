package com.agenda_pro_api.controller;

import com.agenda_pro_api.dto.LoginDTO;
import com.agenda_pro_api.dto.LoginResponse;
import com.agenda_pro_api.dto.RegisterDTO;
import com.agenda_pro_api.dto.UserResponseDTO;
import com.agenda_pro_api.entity.User;
import com.agenda_pro_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 201 Created: convenção REST para "recurso criado com sucesso".
    // Retorna UserResponseDTO (sem senha), nunca a entidade User.
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid RegisterDTO data) {
        UserResponseDTO created = authService.register(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginDTO data) {
        String token = authService.login(data);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/me")
    public UserResponseDTO me(@AuthenticationPrincipal User user) {
        return authService.me(user);
    }
}
