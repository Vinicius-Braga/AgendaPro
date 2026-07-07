package com.agenda_pro_api.service;

import com.agenda_pro_api.entity.Appointment;
import com.agenda_pro_api.entity.AppointmentStatus;
import com.agenda_pro_api.entity.PaymentStatus;
import com.agenda_pro_api.exception.InvalidStateTransitionException;
import com.agenda_pro_api.exception.ResourceNotFoundException;
import com.agenda_pro_api.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;

    public Appointment create(Appointment appointment) {

        boolean hasConflict = repository.existsByUserIdAndDateTime(
                appointment.getUser().getId(),
                appointment.getDateTime()
        );

        if (hasConflict) {
            throw new RuntimeException("Horário já ocupado");
        }

        // O status inicial e o status de pagamento NUNCA vêm do cliente da
        // API — mesmo que o corpo da requisição informe outro valor, ele é
        // ignorado aqui. Todo agendamento nasce agendado e não pago; a partir
        // daí só evolui pelos endpoints de transição (start/complete/cancel/pay).
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setPaymentStatus(PaymentStatus.PENDING);

        return repository.save(appointment);
    }

    public List<Appointment> getDayAgenda(UUID userId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        return repository.findByUserIdAndDateTimeBetween(userId, start, end);
    }

    public List<Appointment> getPending(UUID userId) {
        return repository.findByUserIdAndPaymentStatus(userId, PaymentStatus.PENDING);
    }

    // Inicia o atendimento. Só é permitido a partir de SCHEDULED — não existe
    // relação automática com o horário marcado, pois o profissional pode
    // estar atrasado por causa de um atendimento anterior. Quem decide que
    // "começou de verdade" é o profissional, clicando/chamando este endpoint.
    public Appointment start(UUID id, UUID userId) {
        Appointment appointment = findOwnedOrThrow(id, userId);

        requireStatus(appointment, AppointmentStatus.SCHEDULED,
                "Só é possível iniciar um agendamento com status AGENDADO");

        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        return repository.save(appointment);
    }

    // Conclui o atendimento. Só é permitido a partir de IN_PROGRESS — não dá
    // para "finalizar" algo que nunca foi iniciado.
    public Appointment complete(UUID id, UUID userId) {
        Appointment appointment = findOwnedOrThrow(id, userId);

        requireStatus(appointment, AppointmentStatus.IN_PROGRESS,
                "Só é possível concluir um agendamento EM ATENDIMENTO");

        appointment.setStatus(AppointmentStatus.DONE);
        return repository.save(appointment);
    }

    // Cancela o agendamento. Permitido a partir de SCHEDULED ou IN_PROGRESS,
    // mas não de DONE (não faz sentido cancelar algo já concluído) nem de
    // um CANCELED anterior.
    public Appointment cancel(UUID id, UUID userId) {
        Appointment appointment = findOwnedOrThrow(id, userId);

        boolean canCancel = appointment.getStatus() == AppointmentStatus.SCHEDULED
                || appointment.getStatus() == AppointmentStatus.IN_PROGRESS;

        if (!canCancel) {
            throw new InvalidStateTransitionException(
                    "Não é possível cancelar um agendamento com status " + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.CANCELED);
        return repository.save(appointment);
    }

    // Marca o pagamento como recebido. Independente do status do atendimento
    // (o cliente pode pagar adiantado, antes mesmo do serviço começar).
    public Appointment pay(UUID id, UUID userId) {
        Appointment appointment = findOwnedOrThrow(id, userId);

        if (appointment.getPaymentStatus() == PaymentStatus.PAID) {
            throw new InvalidStateTransitionException("Este agendamento já está pago");
        }

        appointment.setPaymentStatus(PaymentStatus.PAID);
        return repository.save(appointment);
    }

    private Appointment findOwnedOrThrow(UUID id, UUID userId) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));
    }

    private void requireStatus(Appointment appointment, AppointmentStatus expected, String errorMessage) {
        if (appointment.getStatus() != expected) {
            throw new InvalidStateTransitionException(errorMessage
                    + " (status atual: " + appointment.getStatus() + ")");
        }
    }
}
