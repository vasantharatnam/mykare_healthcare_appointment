package com.mykare.appointments.appointment.dto;

import jakarta.validation.constraints.NotNull;

public record CreateAppointmentRequest(
        @NotNull(message = "Slot id is required")
        Long slotId
) {
}