package com.agenda_pro_api.controller;

import com.agenda_pro_api.dto.LoginDTO;
import com.agenda_pro_api.dto.RegisterDTO;
import com.agenda_pro_api.entity.User;
import com.agenda_pro_api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterDTO data) {
        return ResponseEntity.ok(authService.register(data));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO data) {
        return ResponseEntity.ok(authService.login(data));
    }
}