package com.mykare.appointments.auth.dto;

public record AuthResponse(
        Long userId,
        String fullName,
        String email,
        String role,
        String message
) {
}