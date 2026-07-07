package com.agenda_pro_api.service;

import com.agenda_pro_api.dto.AppointmentResponseDTO;
import com.agenda_pro_api.dto.CreateAppointmentRequestDTO;
import com.agenda_pro_api.entity.Appointment;
import com.agenda_pro_api.entity.AppointmentStatus;
import com.agenda_pro_api.entity.Client;
import com.agenda_pro_api.entity.PaymentStatus;
import com.agenda_pro_api.entity.ServiceEntity;
import com.agenda_pro_api.entity.User;
import com.agenda_pro_api.exception.AppointmentConflictException;
import com.agenda_pro_api.exception.InvalidStateTransitionException;
import com.agenda_pro_api.exception.ResourceNotFoundException;
import com.agenda_pro_api.mapper.AppointmentMapper;
import com.agenda_pro_api.repository.AppointmentRepository;
import com.agenda_pro_api.repository.ClientRepository;
import com.agenda_pro_api.repository.ServiceRepository;
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
    private final ClientRepository clientRepository;
    private final ServiceRepository serviceRepository;
    private final AppointmentMapper mapper;

    public AppointmentResponseDTO create(CreateAppointmentRequestDTO dto, User owner) {

        // O cliente e o serviço referenciados precisam pertencer a QUEM está
        // criando o agendamento — sem isso, um usuário poderia agendar
        // usando o cliente/serviço de outra pessoa só sabendo o UUID.
        Client client = clientRepository.findByIdAndUserId(dto.clientId(), owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        ServiceEntity service = serviceRepository.findByIdAndUserId(dto.serviceId(), owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));

        boolean hasConflict = repository.existsByUserIdAndDateTime(owner.getId(), dto.dateTime());
        if (hasConflict) {
            throw new AppointmentConflictException("Horário já ocupado");
        }

        Appointment appointment = mapper.toEntity(dto, owner, client, service);

        // O status inicial e o status de pagamento NUNCA vêm do cliente da
        // API — todo agendamento nasce agendado e não pago; a partir daí só
        // evolui pelos endpoints de transição (start/complete/cancel/pay).
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setPaymentStatus(PaymentStatus.PENDING);

        Appointment saved = repository.save(appointment);
        return mapper.toResponse(saved);
    }

    public List<AppointmentResponseDTO> getDayAgenda(UUID userId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        return repository.findByUserIdAndDateTimeBetween(userId, start, end).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<AppointmentResponseDTO> getPending(UUID userId) {
        return repository.findByUserIdAndPaymentStatus(userId, PaymentStatus.PENDING).stream()
                .map(mapper::toResponse)
                .toList();
    }

    // Inicia o atendimento. Só é permitido a partir de SCHEDULED — não existe
    // relação automática com o horário marcado, pois o profissional pode
    // estar atrasado por causa de um atendimento anterior. Quem decide que
    // "começou de verdade" é o profissional, clicando/chamando este endpoint.
    public AppointmentResponseDTO start(UUID id, UUID userId) {
        Appointment appointment = findOwnedOrThrow(id, userId);

        requireStatus(appointment, AppointmentStatus.SCHEDULED,
                "Só é possível iniciar um agendamento com status AGENDADO");

        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        return mapper.toResponse(repository.save(appointment));
    }

    // Conclui o atendimento. Só é permitido a partir de IN_PROGRESS — não dá
    // para "finalizar" algo que nunca foi iniciado.
    public AppointmentResponseDTO complete(UUID id, UUID userId) {
        Appointment appointment = findOwnedOrThrow(id, userId);

        requireStatus(appointment, AppointmentStatus.IN_PROGRESS,
                "Só é possível concluir um agendamento EM ATENDIMENTO");

        appointment.setStatus(AppointmentStatus.DONE);
        return mapper.toResponse(repository.save(appointment));
    }

    // Cancela o agendamento. Permitido a partir de SCHEDULED ou IN_PROGRESS,
    // mas não de DONE (não faz sentido cancelar algo já concluído) nem de
    // um CANCELED anterior.
    public AppointmentResponseDTO cancel(UUID id, UUID userId) {
        Appointment appointment = findOwnedOrThrow(id, userId);

        boolean canCancel = appointment.getStatus() == AppointmentStatus.SCHEDULED
                || appointment.getStatus() == AppointmentStatus.IN_PROGRESS;

        if (!canCancel) {
            throw new InvalidStateTransitionException(
                    "Não é possível cancelar um agendamento com status " + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.CANCELED);
        return mapper.toResponse(repository.save(appointment));
    }

    // Marca o pagamento como recebido. Independente do status do atendimento
    // (o cliente pode pagar adiantado, antes mesmo do serviço começar).
    public AppointmentResponseDTO pay(UUID id, UUID userId) {
        Appointment appointment = findOwnedOrThrow(id, userId);

        if (appointment.getPaymentStatus() == PaymentStatus.PAID) {
            throw new InvalidStateTransitionException("Este agendamento já está pago");
        }

        appointment.setPaymentStatus(PaymentStatus.PAID);
        return mapper.toResponse(repository.save(appointment));
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
