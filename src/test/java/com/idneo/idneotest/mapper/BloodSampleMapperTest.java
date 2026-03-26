package com.idneo.idneotest.mapper;

import com.idneo.idneotest.domain.model.BloodSample;
import com.idneo.idneotest.domain.model.BloodSampleStatus;
import com.idneo.idneotest.dto.BloodSampleRequestDto;
import com.idneo.idneotest.dto.BloodSampleResponseDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BloodSampleMapperTest {

    private final BloodSampleMapper mapper = Mappers.getMapper(BloodSampleMapper.class);

    @Test
    void shouldMapRequestDtoToEntity() {
        // Given
        UUID patientId = UUID.randomUUID();
        Instant collectedAt = Instant.now();
        BloodSampleRequestDto requestDto = new BloodSampleRequestDto(patientId, collectedAt);

        // When
        BloodSample entity = mapper.toEntity(requestDto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getPatientId()).isEqualTo(patientId);
        assertThat(entity.getCollectedAt()).isEqualTo(collectedAt);
        assertThat(entity.getId()).isNull();
        assertThat(entity.getStatus()).isEqualTo(BloodSampleStatus.REGISTERED); // Default in entity
        assertThat(entity.getProcessedAt()).isNull();
    }

    @Test
    void shouldMapEntityToResponseDto() {
        // Given
        UUID id = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        Instant collectedAt = Instant.now();
        Instant processedAt = Instant.now().plusSeconds(3600);
        BloodSample entity = BloodSample.builder()
                .id(id)
                .patientId(patientId)
                .status(BloodSampleStatus.PROCESSED)
                .collectedAt(collectedAt)
                .processedAt(processedAt)
                .build();

        // When
        BloodSampleResponseDto responseDto = mapper.toResponseDto(entity);

        // Then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.id()).isEqualTo(id);
        assertThat(responseDto.patientId()).isEqualTo(patientId);
        assertThat(responseDto.status()).isEqualTo(BloodSampleStatus.PROCESSED);
        assertThat(responseDto.collectedAt()).isEqualTo(collectedAt);
        assertThat(responseDto.processedAt()).isEqualTo(processedAt);
    }

    @Test
    void shouldReturnNullWhenMappingNullEntity() {
        assertThat(mapper.toResponseDto(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenMappingNullRequestDto() {
        assertThat(mapper.toEntity(null)).isNull();
    }
}
