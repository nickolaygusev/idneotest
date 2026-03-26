package com.idneo.idneotest.repository;

import com.idneo.idneotest.domain.model.BloodSample;
import com.idneo.idneotest.domain.model.BloodSampleStatus;
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
class BloodSampleRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private BloodSampleRepository sampleRepository;

    @Test
    void shouldSaveAndRetrieveSample() {
        // Given
        UUID patientId = UUID.randomUUID();
        BloodSample sample = BloodSample.builder()
                .patientId(patientId)
                .status(BloodSampleStatus.REGISTERED)
                .collectedAt(Instant.now())
                .build();

        // When
        BloodSample savedSample = sampleRepository.save(sample);

        // Then
        assertThat(savedSample.getId()).isNotNull();
        Optional<BloodSample> retrievedSample = sampleRepository.findById(savedSample.getId());
        assertThat(retrievedSample).isPresent();
        assertThat(retrievedSample.get().getPatientId()).isEqualTo(patientId);
        assertThat(retrievedSample.get().getStatus()).isEqualTo(BloodSampleStatus.REGISTERED);
    }

    @Test
    void shouldVerifyDatabaseSchemaMapping() {
        // Given
        UUID patientId = UUID.randomUUID();
        Instant collectedAt = Instant.now().minusSeconds(100);
        Instant processedAt = Instant.now();
        BloodSample sample = BloodSample.builder()
                .patientId(patientId)
                .status(BloodSampleStatus.PROCESSED)
                .collectedAt(collectedAt)
                .processedAt(processedAt)
                .build();

        // When
        BloodSample savedSample = sampleRepository.saveAndFlush(sample);

        // Then
        assertThat(savedSample.getId()).isNotNull();
        BloodSample found = sampleRepository.findById(savedSample.getId()).orElseThrow();
        assertThat(found.getPatientId()).isEqualTo(patientId);
        assertThat(found.getStatus()).isEqualTo(BloodSampleStatus.PROCESSED);
        assertThat(found.getCollectedAt()).isEqualTo(collectedAt);
        assertThat(found.getProcessedAt()).isEqualTo(processedAt);
    }
}
