package com.mykare.appointments.event;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.mykare.appointments.appointment.Appointment;

import org.springframework.stereotype.Component;

@Component
public class AppointmentEventFactory {
    
    public AppointmentEvent create(Appointment appointment, AppointmentEventType eventType){
           return new AppointmentEvent(
                 UUID.randomUUID(),
                 eventType,
                 appointment.getId(),
                 appointment.getUser().getId(),
                 appointment.getDoctor().getId(),
                 appointment.getDoctor().getFullName(),
                 appointment.getDoctor().getSpecialization(),
                 appointment.getSlot().getId(),
                 appointment.getSlot().getSlotStart(),
                 appointment.getSlot().getSlotEnd(),
                 appointment.getStatus().name(),
                 appointment.getProcessingStatus().name(),
                 OffsetDateTime.now()
           );
    }

}
