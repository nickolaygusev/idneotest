package com.idneo.idneotest.controller;

import com.idneo.idneotest.domain.model.BloodSampleStatus;
import com.idneo.idneotest.dto.BloodSampleRequestDto;
import com.idneo.idneotest.dto.BloodSampleResponseDto;
import com.idneo.idneotest.repository.BloodSampleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebTestClient
class BloodSampleIntegrationTest {

    // PostgreSQL container with automatic Spring Boot integration
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BloodSampleRepository sampleRepository;

    @BeforeEach
    void setUp() {
        sampleRepository.deleteAll(); // clean DB before each test
        // TODO delete
        System.out.println("JDBC URL: " + postgres.getJdbcUrl());
        System.out.println("Username: " + postgres.getUsername());
        System.out.println("Password: " + postgres.getPassword());
    }

    @Test
    void registerAndProcessBloodSampleIntegration() {
        // 1. Register a blood sample
        UUID patientId = UUID.randomUUID();
        Instant collectedAt = Instant.now();
        BloodSampleRequestDto requestDto = new BloodSampleRequestDto(patientId, collectedAt);

        BloodSampleResponseDto registeredSample = webTestClient.post()
                .uri("/blood-sample")
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BloodSampleResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(registeredSample).isNotNull();
        assertThat(registeredSample.id()).isNotNull();
        assertThat(registeredSample.patientId()).isEqualTo(patientId);
        assertThat(registeredSample.status()).isEqualTo(BloodSampleStatus.REGISTERED);

        // 2. Retrieve the sample with filters
        BloodSampleResponseDto[] samples = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/blood-sample")
                        .queryParam("patientId", patientId)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(BloodSampleResponseDto[].class)
                .returnResult()
                .getResponseBody();

        assertThat(samples).isNotNull().hasSize(1);
        assertThat(samples[0].id()).isEqualTo(registeredSample.id());

        // 3. Process the sample
        BloodSampleResponseDto processedSample = webTestClient.patch()
                .uri("/blood-sample/{id}/process", registeredSample.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody(BloodSampleResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(processedSample).isNotNull();
        assertThat(processedSample.status()).isEqualTo(BloodSampleStatus.PROCESSED);
        assertThat(processedSample.processedAt()).isNotNull();

        // 4. Verify DB interaction
        assertThat(sampleRepository.findById(registeredSample.id())).isPresent()
                .hasValueSatisfying(s -> {
                    assertThat(s.getStatus()).isEqualTo(BloodSampleStatus.PROCESSED);
                    assertThat(s.getProcessedAt()).isNotNull();
                });
    }

    @Test
    void shouldReturnErrorWhenRegisteringInvalidSample() {
        BloodSampleRequestDto invalidRequest = new BloodSampleRequestDto(null, null);

        webTestClient.post()
                .uri("/blood-sample")
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturn404WhenProcessingNonExistentSample() {
        UUID nonExistentId = UUID.randomUUID();

        webTestClient.patch()
                .uri("/blood-sample/{id}/process", nonExistentId)
                .exchange()
                .expectStatus().isNotFound();
    }
}