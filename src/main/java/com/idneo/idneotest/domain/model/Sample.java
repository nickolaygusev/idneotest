package com.idneo.idneotest.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "samples", indexes = {
        @Index(name = "idx_patient_id", columnList = "patientId"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_collected_at", columnList = "collectedAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sample {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID patientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SampleStatus status = SampleStatus.REGISTERED;

    @Column(nullable = false)
    private Instant collectedAt;

    @Column
    private Instant processedAt;
}
