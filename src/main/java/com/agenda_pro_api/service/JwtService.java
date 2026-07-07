package com.agenda_pro_api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final String secret;
    private final long expirationMillis;

    // @Value lê valores do application.yaml (que por sua vez pode vir de
    // variáveis de ambiente, ex.: ${JWT_SECRET:...}). Assim o segredo do JWT
    // fica configurável por ambiente (dev/prod) sem tocar no código-fonte —
    // e nunca mais fica hardcoded, o que é essencial: se o segredo vazasse
    // no repositório, qualquer pessoa poderia forjar tokens válidos.
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMillis
    ) {
        this.secret = secret;
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(String email) {

        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withIssuer("agenda-pro-api")
                .withSubject(email)
                .withExpiresAt(generateExpirationDate())
                .sign(algorithm);
    }

    public String validateToken(String token) {

        try {

            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer("agenda-pro-api")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    private Instant generateExpirationDate() {
        // Instant já é UTC por definição — somar millis direto evita bugs de
        // fuso horário que existiam antes (offset "-03:00" fixo no código).
        return Instant.now().plusMillis(expirationMillis);
    }
}
