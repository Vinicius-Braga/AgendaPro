package com.agenda_pro_api.exception;

/**
 * Lançada quando um recurso não existe OU não pertence ao usuário autenticado.
 *
 * Propositalmente usamos a mesma exceção (e 404) para os dois casos — dizer
 * "esse cliente é de outro usuário" (403) revelaria que o ID existe no banco,
 * vazando informação sobre dados de terceiros.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
