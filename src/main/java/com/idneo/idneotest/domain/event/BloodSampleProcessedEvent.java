package com.idneo.idneotest.domain.event;

import java.time.Instant;
import java.util.UUID;

public record BloodSampleProcessedEvent(
    UUID sampleId,
    UUID patientId,
    Instant processedAt
) {}
