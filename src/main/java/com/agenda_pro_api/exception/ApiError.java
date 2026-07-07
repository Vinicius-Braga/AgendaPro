package com.agenda_pro_api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

/**
 * Formato ÚNICO de resposta de erro da API. Todo erro tratado sai neste shape,
 * o que facilita a vida de quem consome a API (frontend, mobile, testes).
 *
 * @JsonInclude(NON_NULL): o campo "fields" só aparece no JSON quando existe
 * (erros de validação). Nos demais erros ele é omitido em vez de sair "null".
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        int status,
        String message,
        Instant timestamp,
        Map<String, String> fields
) {
    public static ApiError of(int status, String message) {
        return new ApiError(status, message, Instant.now(), null);
    }

    public static ApiError of(int status, String message, Map<String, String> fields) {
        return new ApiError(status, message, Instant.now(), fields);
    }
}
