package com.agenda_pro_api.service;

import com.agenda_pro_api.dto.LoginDTO;
import com.agenda_pro_api.dto.RegisterDTO;
import com.agenda_pro_api.dto.UserResponseDTO;
import com.agenda_pro_api.entity.User;
import com.agenda_pro_api.exception.EmailAlreadyExistsException;
import com.agenda_pro_api.exception.InvalidCredentialsException;
import com.agenda_pro_api.mapper.UserMapper;
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
    private final UserMapper userMapper;

    public UserResponseDTO register(RegisterDTO data) {

        // E-mail é normalizado para minúsculas ANTES de qualquer consulta/gravação.
        // Sem isso, "Joao@mail.com" e "joao@mail.com" seriam tratados como
        // e-mails diferentes — permitindo cadastro duplicado e exigindo que o
        // usuário digite o e-mail com a capitalização exata no login.
        String normalizedEmail = normalizeEmail(data.email());

        // Regra de negócio: e-mail é único. Checamos ANTES de salvar para
        // devolver um erro claro (409) em vez de estourar a constraint do banco.
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new EmailAlreadyExistsException(normalizedEmail);
        }

        User user = userMapper.toEntity(data);
        user.setEmail(normalizedEmail);

        // Criptografia é responsabilidade do Service. Nunca salvamos senha crua.
        user.setPassword(passwordEncoder.encode(data.password()));

        User savedUser = userRepository.save(user);

        // Devolve DTO sem a senha — nunca expor a entidade diretamente.
        return userMapper.toResponse(savedUser);
    }

    public String login(LoginDTO data) {

        String normalizedEmail = normalizeEmail(data.email());

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(InvalidCredentialsException::new);

        boolean passwordMatches =
                passwordEncoder.matches(data.password(), user.getPassword());

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        return jwtService.generateToken(user.getEmail());
    }

    // O front precisa exibir o nome do usuário logado (ex.: nome do negócio no
    // cabeçalho/sidebar) sem ter que decodificar isso do JWT — o token só
    // carrega o e-mail (subject). @AuthenticationPrincipal já entrega o User
    // completo, então é só mapear.
    public UserResponseDTO me(User user) {
        return userMapper.toResponse(user);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
