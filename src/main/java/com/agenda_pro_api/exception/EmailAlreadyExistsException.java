package com.agenda_pro_api.exception;

/**
 * Lançada quando se tenta cadastrar um usuário com um e-mail que já existe.
 *
 * Por que uma exceção própria (e não RuntimeException genérica)?
 * - Dá SIGNIFICADO ao erro: quem lê o código sabe exatamente o que aconteceu.
 * - Permite que o @RestControllerAdvice trate ESTE caso de forma específica
 *   (retornar 409 Conflict), sem misturar com outros erros.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("E-mail já cadastrado: " + email);
    }
}
