package com.mykare.appointments.appointment;

import java.time.OffsetDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "appointment_logs")
public class AppointmentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Column(name = "event_type", nullable = false, length = 80)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected AppointmentLog() {
    }

    public AppointmentLog(Appointment appointment, String eventType, String message) {
        this.appointment = appointment;
        this.eventType = eventType;
        this.message = message;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public String getEventType() {
        return eventType;
    }

    public String getMessage() {
        return message;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}