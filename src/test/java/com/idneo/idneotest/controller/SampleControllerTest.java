package com.idneo.idneotest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.idneo.idneotest.domain.model.SampleStatus;
import com.idneo.idneotest.dto.SampleRequestDto;
import com.idneo.idneotest.dto.SampleResponseDto;
import com.idneo.idneotest.service.SampleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SampleController.class)
@Import(com.fasterxml.jackson.databind.ObjectMapper.class)
class SampleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SampleService sampleService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void registerSampleShouldReturn201() throws Exception {
        UUID patientId = UUID.randomUUID();
        Instant collectedAt = Instant.now();
        SampleRequestDto requestDto = new SampleRequestDto(patientId, collectedAt);
        SampleResponseDto responseDto = new SampleResponseDto(UUID.randomUUID(), patientId, SampleStatus.REGISTERED, collectedAt, null);

        when(sampleService.registerSample(any(SampleRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/sample")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.patientId").value(patientId.toString()))
                .andExpect(jsonPath("$.status").value("REGISTERED"));
    }

    @Test
    void registerSampleShouldReturn400WhenInvalidRequest() throws Exception {
        SampleRequestDto invalidRequest = new SampleRequestDto(null, null);

        mockMvc.perform(post("/sample")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSamplesShouldReturnList() throws Exception {
        UUID patientId = UUID.randomUUID();
        SampleResponseDto responseDto = new SampleResponseDto(UUID.randomUUID(), patientId, SampleStatus.REGISTERED, Instant.now(), null);
        
        when(sampleService.getSamples(any(), any(), any(), any())).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/sample")
                        .param("patientId", patientId.toString())
                        .param("status", "REGISTERED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(patientId.toString()))
                .andExpect(jsonPath("$[0].status").value("REGISTERED"));
    }

    @Test
    void processSampleShouldReturn200() throws Exception {
        UUID sampleId = UUID.randomUUID();
        SampleResponseDto responseDto = new SampleResponseDto(sampleId, UUID.randomUUID(), SampleStatus.PROCESSED, Instant.now(), Instant.now());

        when(sampleService.processSample(eq(sampleId))).thenReturn(responseDto);

        mockMvc.perform(patch("/sample/{id}/process", sampleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleId.toString()))
                .andExpect(jsonPath("$.status").value("PROCESSED"))
                .andExpect(jsonPath("$.processedAt").exists());
    }
}
