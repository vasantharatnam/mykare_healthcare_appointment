package com.mykare.appointments.doctor.dto;

public record DoctorResponse(
        Long id,
        String fullName,
        String specialization
) {
}