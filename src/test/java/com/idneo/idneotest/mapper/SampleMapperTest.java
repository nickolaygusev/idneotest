package com.idneo.idneotest.mapper;

import com.idneo.idneotest.domain.model.Sample;
import com.idneo.idneotest.domain.model.SampleStatus;
import com.idneo.idneotest.dto.SampleRequestDto;
import com.idneo.idneotest.dto.SampleResponseDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SampleMapperTest {

    private final SampleMapper mapper = Mappers.getMapper(SampleMapper.class);

    @Test
    void shouldMapRequestDtoToEntity() {
        // Given
        UUID patientId = UUID.randomUUID();
        Instant collectedAt = Instant.now();
        SampleRequestDto requestDto = new SampleRequestDto(patientId, collectedAt);

        // When
        Sample entity = mapper.toEntity(requestDto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getPatientId()).isEqualTo(patientId);
        assertThat(entity.getCollectedAt()).isEqualTo(collectedAt);
        assertThat(entity.getId()).isNull();
        assertThat(entity.getStatus()).isEqualTo(SampleStatus.REGISTERED); // Default in entity
        assertThat(entity.getProcessedAt()).isNull();
    }

    @Test
    void shouldMapEntityToResponseDto() {
        // Given
        UUID id = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        Instant collectedAt = Instant.now();
        Instant processedAt = Instant.now().plusSeconds(3600);
        Sample entity = Sample.builder()
                .id(id)
                .patientId(patientId)
                .status(SampleStatus.PROCESSED)
                .collectedAt(collectedAt)
                .processedAt(processedAt)
                .build();

        // When
        SampleResponseDto responseDto = mapper.toResponseDto(entity);

        // Then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.id()).isEqualTo(id);
        assertThat(responseDto.patientId()).isEqualTo(patientId);
        assertThat(responseDto.status()).isEqualTo(SampleStatus.PROCESSED);
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
