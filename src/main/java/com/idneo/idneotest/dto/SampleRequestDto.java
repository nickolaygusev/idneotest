package com.idneo.idneotest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;
import java.util.UUID;

public record SampleRequestDto(
    @NotNull(message = "patientId must not be null")
    UUID patientId,
    
    @NotNull(message = "collectedAt must not be null")
    @PastOrPresent(message = "collectedAt must not be in the future")
    Instant collectedAt
) {}
