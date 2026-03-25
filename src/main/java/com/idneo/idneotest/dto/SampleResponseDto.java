package com.idneo.idneotest.dto;

import com.idneo.idneotest.domain.model.SampleStatus;
import java.time.Instant;
import java.util.UUID;

public record SampleResponseDto(
    UUID id,
    UUID patientId,
    SampleStatus status,
    Instant collectedAt,
    Instant processedAt
) {}
