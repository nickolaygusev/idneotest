package com.idneo.idneotest.repository;

import com.idneo.idneotest.domain.model.Sample;
import com.idneo.idneotest.domain.model.SampleStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SampleRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private SampleRepository sampleRepository;

    @Test
    void shouldSaveAndRetrieveSample() {
        // Given
        UUID patientId = UUID.randomUUID();
        Sample sample = Sample.builder()
                .patientId(patientId)
                .status(SampleStatus.REGISTERED)
                .collectedAt(Instant.now())
                .build();

        // When
        Sample savedSample = sampleRepository.save(sample);

        // Then
        assertThat(savedSample.getId()).isNotNull();
        Optional<Sample> retrievedSample = sampleRepository.findById(savedSample.getId());
        assertThat(retrievedSample).isPresent();
        assertThat(retrievedSample.get().getPatientId()).isEqualTo(patientId);
        assertThat(retrievedSample.get().getStatus()).isEqualTo(SampleStatus.REGISTERED);
    }

    @Test
    void shouldVerifyDatabaseSchemaMapping() {
        // Given
        UUID patientId = UUID.randomUUID();
        Instant collectedAt = Instant.now().minusSeconds(100);
        Instant processedAt = Instant.now();
        Sample sample = Sample.builder()
                .patientId(patientId)
                .status(SampleStatus.PROCESSED)
                .collectedAt(collectedAt)
                .processedAt(processedAt)
                .build();

        // When
        Sample savedSample = sampleRepository.saveAndFlush(sample);

        // Then
        assertThat(savedSample.getId()).isNotNull();
        Sample found = sampleRepository.findById(savedSample.getId()).orElseThrow();
        assertThat(found.getPatientId()).isEqualTo(patientId);
        assertThat(found.getStatus()).isEqualTo(SampleStatus.PROCESSED);
        assertThat(found.getCollectedAt()).isEqualTo(collectedAt);
        assertThat(found.getProcessedAt()).isEqualTo(processedAt);
    }
}
