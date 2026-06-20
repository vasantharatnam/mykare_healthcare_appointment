package com.mykare.appointments.appointment.dto;

import java.time.OffsetDateTime;

public record AppointmentResponse(
        Long appointmentId,
        Long slotId,
        Long doctorId,
        String doctorName,
        String specialization,
        OffsetDateTime slotStart,
        OffsetDateTime slotEnd,
        String status,
        String processingStatus,
        OffsetDateTime createdAt
) {
}