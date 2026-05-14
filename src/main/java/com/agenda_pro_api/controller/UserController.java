package com.agenda_pro_api.controller;

import com.agenda_pro_api.entity.User;
import com.agenda_pro_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public User create(@RequestBody User user) {

        String encryptedPassword =
                passwordEncoder.encode(user.getPassword());

        user.setPassword(encryptedPassword);

        return repository.save(user);
    }

    @GetMapping
    public List<User> list() {
        return repository.findAll();
    }
}