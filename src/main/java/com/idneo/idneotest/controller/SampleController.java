package com.idneo.idneotest.controller;

import com.idneo.idneotest.domain.model.SampleStatus;
import com.idneo.idneotest.dto.SampleRequestDto;
import com.idneo.idneotest.dto.SampleResponseDto;
import com.idneo.idneotest.service.SampleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sample")
@RequiredArgsConstructor
public class SampleController {

    private final SampleService sampleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SampleResponseDto registerSample(@Valid @RequestBody SampleRequestDto requestDto) {
        return sampleService.registerSample(requestDto);
    }

    @GetMapping
    public List<SampleResponseDto> getSamples(
            @RequestParam(required = false) SampleStatus status,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
        return sampleService.getSamples(status, patientId, fromDate, toDate);
    }

    @PatchMapping("/{id}/process")
    public SampleResponseDto processSample(@PathVariable UUID id) {
        return sampleService.processSample(id);
    }
}
