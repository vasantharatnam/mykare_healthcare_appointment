package com.mykare.appointments.slot.dto;

import java.time.OffsetDateTime;

public record AvailableSlotResponse(
        Long slotId,
        Long doctorId,
        String doctorName,
        String specialization,
        OffsetDateTime slotStart,
        OffsetDateTime slotEnd
) {
}