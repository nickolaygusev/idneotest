package com.idneo.idneotest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.idneo.idneotest.domain.exception.BloodSampleAlreadyProcessedException;
import com.idneo.idneotest.domain.exception.BloodSampleNotFoundException;
import com.idneo.idneotest.domain.model.BloodSampleStatus;
import com.idneo.idneotest.dto.BloodSampleRequestDto;
import com.idneo.idneotest.dto.BloodSampleResponseDto;
import com.idneo.idneotest.service.BloodSampleService;
import com.idneo.idneotest.domain.exception.GlobalExceptionHandler;
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

@WebMvcTest(BloodSampleController.class)
@Import({ObjectMapper.class, GlobalExceptionHandler.class})
class BloodSampleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BloodSampleService sampleService;

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
        BloodSampleRequestDto requestDto = new BloodSampleRequestDto(patientId, collectedAt);
        BloodSampleResponseDto responseDto = new BloodSampleResponseDto(UUID.randomUUID(), patientId, BloodSampleStatus.REGISTERED, collectedAt, null);

        when(sampleService.registerSample(any(BloodSampleRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/blood-sample")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.patientId").value(patientId.toString()))
                .andExpect(jsonPath("$.status").value("REGISTERED"));
    }


    @Test
    void getSamplesShouldReturnList() throws Exception {
        UUID patientId = UUID.randomUUID();
        BloodSampleResponseDto responseDto = new BloodSampleResponseDto(UUID.randomUUID(), patientId, BloodSampleStatus.REGISTERED, Instant.now(), null);
        
        when(sampleService.getSamples(any(), any(), any(), any())).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/blood-sample")
                        .param("patientId", patientId.toString())
                        .param("status", "REGISTERED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(patientId.toString()))
                .andExpect(jsonPath("$[0].status").value("REGISTERED"));
    }

    @Test
    void processSampleShouldReturn200() throws Exception {
        UUID sampleId = UUID.randomUUID();
        BloodSampleResponseDto responseDto = new BloodSampleResponseDto(sampleId, UUID.randomUUID(), BloodSampleStatus.PROCESSED, Instant.now(), Instant.now());

        when(sampleService.processSample(eq(sampleId))).thenReturn(responseDto);

        mockMvc.perform(patch("/blood-sample/{id}/process", sampleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleId.toString()))
                .andExpect(jsonPath("$.status").value("PROCESSED"))
                .andExpect(jsonPath("$.processedAt").exists());
    }

    @Test
    void processSampleShouldReturn404WhenNotFound() throws Exception {
        UUID sampleId = UUID.randomUUID();
        when(sampleService.processSample(eq(sampleId))).thenThrow(new BloodSampleNotFoundException("Not found"));

        mockMvc.perform(patch("/blood-sample/{id}/process", sampleId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Not found"))
                .andExpect(jsonPath("$.path").value("/blood-sample/" + sampleId + "/process"));
    }

    @Test
    void processSampleShouldReturn409WhenAlreadyProcessed() throws Exception {
        UUID sampleId = UUID.randomUUID();
        when(sampleService.processSample(eq(sampleId))).thenThrow(new BloodSampleAlreadyProcessedException("Already processed"));

        mockMvc.perform(patch("/blood-sample/{id}/process", sampleId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("ALREADY_PROCESSED"))
                .andExpect(jsonPath("$.message").value("Already processed"));
    }

    @Test
    void registerSampleShouldReturn400WithErrorResponse() throws Exception {
        BloodSampleRequestDto invalidRequest = new BloodSampleRequestDto(null, null);

        mockMvc.perform(post("/blood-sample")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").exists());
    }
}
