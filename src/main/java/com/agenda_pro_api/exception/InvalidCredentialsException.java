package com.agenda_pro_api.exception;

/**
 * Lançada no login quando o e-mail não existe OU a senha está incorreta.
 *
 * Segurança: usamos a MESMA exceção e a MESMA mensagem para os dois casos.
 * Se disséssemos "usuário não encontrado" vs "senha inválida", um atacante
 * poderia descobrir quais e-mails estão cadastrados (enumeração de usuários).
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Credenciais inválidas");
    }
}
