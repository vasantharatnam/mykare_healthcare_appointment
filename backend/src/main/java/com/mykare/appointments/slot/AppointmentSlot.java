package com.mykare.appointments.slot;

import java.time.OffsetDateTime;

import com.mykare.appointments.doctor.Doctor;

import jakarta.persistence.*;

@Entity
@Table(name = "appointment_slots")
public class AppointmentSlot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "slot_start" , nullable = false)
    private OffsetDateTime slotStart;

    @Column(name = "slot_end", nullable = false)
    private OffsetDateTime slotEnd;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected AppointmentSlot() {
    }

    public Long getId() {
        return id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public OffsetDateTime getSlotStart() {
        return slotStart;
    }

    public OffsetDateTime getSlotEnd() {
        return slotEnd;
    }

    public boolean isActive() {
        return active;
    }
}


