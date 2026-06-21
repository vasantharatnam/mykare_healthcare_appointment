package com.mykare.appointments.event;

import java.time.OffsetDateTime;
import java.util.UUID;


public record AppointmentEvent (
    UUID eventId,
    AppointmentEventType eventType,
    Long appointmentId,
    Long userId,
    Long doctorId,
    String doctorName,
    String specialization,
    Long slotId,
    OffsetDateTime slotStart,
    OffsetDateTime slotEnd,
    String status,
    String processingStatus,
    OffsetDateTime occuredAt
){

}
    

