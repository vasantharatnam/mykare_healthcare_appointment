package com.mykare.appointments.appointment;

import java.time.OffsetDateTime;

import com.mykare.appointments.doctor.Doctor;
import com.mykare.appointments.slot.AppointmentSlot;
import com.mykare.appointments.user.User;

import jakarta.persistence.*;

@Entity
@Table(name = "appointments")

public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id" , nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private AppointmentSlot slot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false, length = 20)
    private ProcessingStatus processingStatus = ProcessingStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "cancelled_at")
    private OffsetDateTime cancelledAt;

    protected Appointment() {
    }

    public Appointment(User user, Doctor doctor, AppointmentSlot slot) {
        this.user = user;
        this.doctor = doctor;
        this.slot = slot;
        this.status = AppointmentStatus.BOOKED;
        this.processingStatus = ProcessingStatus.PENDING;
    }

    public void cancel(String reason) {
    this.status = AppointmentStatus.CANCELLED;
    this.reason = reason;
    this.cancelledAt = OffsetDateTime.now();
   }
   
    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public AppointmentSlot getSlot() {
        return slot;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public String getReason() {
        return reason;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getCancelledAt() {
        return cancelledAt;
    }

    public OffsetDateTime getUpdatedAt() {
    return updatedAt;
    }
}
