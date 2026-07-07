package com.agenda_pro_api.mapper;

import com.agenda_pro_api.dto.AppointmentResponseDTO;
import com.agenda_pro_api.dto.CreateAppointmentRequestDTO;
import com.agenda_pro_api.entity.Appointment;
import com.agenda_pro_api.entity.Client;
import com.agenda_pro_api.entity.ServiceEntity;
import com.agenda_pro_api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentMapper {

    private final ClientMapper clientMapper;
    private final ServiceMapper serviceMapper;

    // Status e status de pagamento NÃO são setados aqui — quem decide os
    // valores iniciais é o AppointmentService (SCHEDULED/PENDING sempre),
    // mantendo essa regra de negócio fora do mapper.
    public Appointment toEntity(CreateAppointmentRequestDTO dto, User owner, Client client, ServiceEntity service) {
        Appointment appointment = new Appointment();
        appointment.setUser(owner);
        appointment.setClient(client);
        appointment.setService(service);
        appointment.setDateTime(dto.dateTime());
        appointment.setPrice(dto.price());
        appointment.setNotes(dto.notes());
        return appointment;
    }

    public AppointmentResponseDTO toResponse(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(),
                clientMapper.toResponse(appointment.getClient()),
                serviceMapper.toResponse(appointment.getService()),
                appointment.getDateTime(),
                appointment.getStatus(),
                appointment.getPaymentStatus(),
                appointment.getPrice(),
                appointment.getNotes(),
                appointment.getCreatedAt()
        );
    }
}
