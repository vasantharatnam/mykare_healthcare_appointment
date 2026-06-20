package com.mykare.appointments.appointment.dto;

import jakarta.validation.constraints.Size;

public record CancelAppointmentRequest(
        @Size(max = 500, message = "Reason must be at most 500 characters")
        String reason
) {
}