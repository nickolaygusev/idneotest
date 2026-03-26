package com.idneo.idneotest.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "blood_samples", indexes = {
        @Index(name = "idx_patient_id", columnList = "patient_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_collected_at", columnList = "collected_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodSample {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BloodSampleStatus status = BloodSampleStatus.REGISTERED;

    @Column(name = "collected_at", nullable = false)
    private Instant collectedAt;

    @Column(name = "processed_at")
    private Instant processedAt;
}
