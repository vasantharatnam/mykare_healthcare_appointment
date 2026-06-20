package com.mykare.appointments.appointment.dto;

import java.time.OffsetDateTime;

public record AppointmentLogResponse(
        Long id,
        String eventType,
        String message,
        OffsetDateTime createdAt
) {
}