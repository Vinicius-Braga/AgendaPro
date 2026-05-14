package com.agenda_pro_api.service;

import com.agenda_pro_api.dto.LoginDTO;
import com.agenda_pro_api.dto.RegisterDTO;
import com.agenda_pro_api.entity.User;
import com.agenda_pro_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User register(RegisterDTO data) {

        User user = new User();

        user.setName(data.name());
        user.setEmail(data.email());

        String encryptedPassword =
                passwordEncoder.encode(data.password());

        user.setPassword(encryptedPassword);

        return userRepository.save(user);
    }

    public String login(LoginDTO data) {

        User user = userRepository.findByEmail(data.email())
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado"));

        boolean passwordMatches =
                passwordEncoder.matches(
                        data.password(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new RuntimeException("Senha inválida");
        }

        return jwtService.generateToken(user.getEmail());
    }
}