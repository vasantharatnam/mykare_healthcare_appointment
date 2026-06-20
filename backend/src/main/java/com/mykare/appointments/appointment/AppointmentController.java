package com.mykare.appointments.appointment;

import com.mykare.appointments.appointment.dto.AppointmentResponse;
import com.mykare.appointments.appointment.dto.CreateAppointmentRequest;
import com.mykare.appointments.security.CurrentUser;
import com.mykare.appointments.security.UserPrincipal;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CreateAppointmentRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(appointmentService.bookAppointment(currentUser.getId(), request));
    }
}