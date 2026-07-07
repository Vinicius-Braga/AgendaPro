package com.agenda_pro_api.exception;

/**
 * Lançada quando já existe um agendamento para o mesmo usuário no mesmo
 * horário exato.
 */
public class AppointmentConflictException extends RuntimeException {

    public AppointmentConflictException(String message) {
        super(message);
    }
}
