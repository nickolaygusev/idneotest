package com.idneo.idneotest.controller;

import com.idneo.idneotest.domain.model.BloodSampleStatus;
import com.idneo.idneotest.dto.BloodSampleRequestDto;
import com.idneo.idneotest.dto.BloodSampleResponseDto;
import com.idneo.idneotest.service.BloodSampleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/blood-sample")
@RequiredArgsConstructor
@Tag(name = "Blood Sample Management", description = "Endpoints for managing clinical blood samples")
public class BloodSampleController {

    private final BloodSampleService sampleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new blood sample", description = "Creates a new blood sample record with status REGISTERED")
    public BloodSampleResponseDto registerSample(@Valid @RequestBody BloodSampleRequestDto requestDto) {
        return sampleService.registerSample(requestDto);
    }

    @GetMapping
    @Operation(summary = "Retrieve blood samples", description = "Returns a list of blood samples filtered by status, patientId, or date range")
    public List<BloodSampleResponseDto> getSamples(
            @RequestParam(required = false) BloodSampleStatus status,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
        return sampleService.getSamples(status, patientId, fromDate, toDate);
    }

    @PatchMapping("/{id}/process")
    @Operation(summary = "Process a blood sample", description = "Marks a registered blood sample as PROCESSED and sets the processing timestamp")
    public BloodSampleResponseDto processSample(@PathVariable UUID id) {
        return sampleService.processSample(id);
    }
}
