package com.agenda_pro_api.entity;

/**
 * Ciclo de vida do agendamento. Transições permitidas (ver AppointmentService):
 * SCHEDULED -> IN_PROGRESS -> DONE
 * SCHEDULED -> CANCELED
 * IN_PROGRESS -> CANCELED
 *
 * Nunca é definido diretamente pelo cliente da API — sempre começa em
 * SCHEDULED na criação e muda apenas via endpoints de transição explícita
 * (/start, /complete, /cancel), acionados pelo profissional.
 */
public enum AppointmentStatus {
    SCHEDULED,
    IN_PROGRESS,
    DONE,
    CANCELED
}