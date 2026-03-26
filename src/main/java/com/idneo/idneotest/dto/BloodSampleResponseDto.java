package com.idneo.idneotest.dto;

import com.idneo.idneotest.domain.model.BloodSampleStatus;
import java.time.Instant;
import java.util.UUID;

public record BloodSampleResponseDto(
    UUID id,
    UUID patientId,
    BloodSampleStatus status,
    Instant collectedAt,
    Instant processedAt
) {}
