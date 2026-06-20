package com.mykare.appointments.appointment;

import java.util.List;

import com.mykare.appointments.appointment.dto.AppointmentLogResponse;
import com.mykare.appointments.appointment.dto.AppointmentResponse;
import com.mykare.appointments.appointment.dto.CancelAppointmentRequest;
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

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long id,
            @Valid @RequestBody CancelAppointmentRequest request
    ) {
        return ResponseEntity.ok(
                appointmentService.cancelAppointment(currentUser.getId(), id, request)
        );
    }

     @GetMapping("/my")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(
            @CurrentUser UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(
                appointmentService.getMyAppointments(currentUser.getId())
        );
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<List<AppointmentLogResponse>> getAppointmentLogs(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                appointmentService.getAppointmentLogs(currentUser.getId(), id)
        );
    }
}