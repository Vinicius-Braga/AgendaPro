package com.agenda_pro_api.entity;

/**
 * Status financeiro do agendamento — independente do status do atendimento
 * (AppointmentStatus). Um cliente pode pagar adiantado (PAID com o
 * agendamento ainda SCHEDULED) ou pagar só depois de FINALIZADO.
 */
public enum PaymentStatus {
    PENDING,
    PAID
}
