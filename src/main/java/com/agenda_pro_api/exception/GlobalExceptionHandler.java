package com.agenda_pro_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Ponto ÚNICO de tratamento de exceções da API (passo 5 do roadmap).
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody: intercepta exceções
 * lançadas por QUALQUER controller e devolve o corpo já serializado em JSON.
 *
 * Vantagem: os Services só precisam LANÇAR a exceção certa (throw). Eles não
 * sabem — e não devem saber — nada sobre HTTP/status code. A tradução
 * "exceção de negócio -> resposta HTTP" fica centralizada aqui.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 409 Conflict: o recurso conflita com o estado atual (e-mail já existe).
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity
                .status(status)
                .body(ApiError.of(status.value(), ex.getMessage()));
    }

    // 401 Unauthorized: credenciais inválidas no login.
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return ResponseEntity
                .status(status)
                .body(ApiError.of(status.value(), ex.getMessage()));
    }

    // 404 Not Found: recurso inexistente ou pertencente a outro usuário.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity
                .status(status)
                .body(ApiError.of(status.value(), ex.getMessage()));
    }

    // 409 Conflict: a transição de estado pedida não é válida a partir do estado atual.
    @ExceptionHandler(InvalidStateTransitionException.class)
    public ResponseEntity<ApiError> handleInvalidStateTransition(InvalidStateTransitionException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity
                .status(status)
                .body(ApiError.of(status.value(), ex.getMessage()));
    }

    // 409 Conflict: já existe um agendamento no mesmo horário para o usuário.
    @ExceptionHandler(AppointmentConflictException.class)
    public ResponseEntity<ApiError> handleAppointmentConflict(AppointmentConflictException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity
                .status(status)
                .body(ApiError.of(status.value(), ex.getMessage()));
    }

    // 400 Bad Request: falha nas validações do @Valid (@NotBlank, @Email, @Size...).
    // O Spring lança MethodArgumentNotValidException; aqui extraímos campo -> mensagem.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fields.put(error.getField(), error.getDefaultMessage());
        }

        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(status)
                .body(ApiError.of(status.value(), "Dados inválidos", fields));
    }
}
