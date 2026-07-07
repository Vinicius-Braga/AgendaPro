package com.agenda_pro_api.exception;

/**
 * Lançada ao tentar mudar o status de um agendamento (ou pagamento) para um
 * estado que não é alcançável a partir do estado atual — ex.: concluir um
 * agendamento que ainda não foi iniciado.
 */
public class InvalidStateTransitionException extends RuntimeException {

    public InvalidStateTransitionException(String message) {
        super(message);
    }
}
