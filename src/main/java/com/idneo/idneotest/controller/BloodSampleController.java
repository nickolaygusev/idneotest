package com.idneo.idneotest.controller;

import com.idneo.idneotest.domain.model.BloodSampleStatus;
import com.idneo.idneotest.dto.BloodSampleRequestDto;
import com.idneo.idneotest.dto.BloodSampleResponseDto;
import com.idneo.idneotest.service.BloodSampleService;
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
public class BloodSampleController {

    private final BloodSampleService sampleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BloodSampleResponseDto registerSample(@Valid @RequestBody BloodSampleRequestDto requestDto) {
        return sampleService.registerSample(requestDto);
    }

    @GetMapping
    public List<BloodSampleResponseDto> getSamples(
            @RequestParam(required = false) BloodSampleStatus status,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
        return sampleService.getSamples(status, patientId, fromDate, toDate);
    }

    @PatchMapping("/{id}/process")
    public BloodSampleResponseDto processSample(@PathVariable UUID id) {
        return sampleService.processSample(id);
    }
}
