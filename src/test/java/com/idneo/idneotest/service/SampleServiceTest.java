package com.idneo.idneotest.service;

import com.idneo.idneotest.domain.exception.SampleAlreadyProcessedException;
import com.idneo.idneotest.domain.exception.SampleNotFoundException;
import com.idneo.idneotest.domain.model.Sample;
import com.idneo.idneotest.domain.model.SampleStatus;
import com.idneo.idneotest.dto.SampleRequestDto;
import com.idneo.idneotest.dto.SampleResponseDto;
import com.idneo.idneotest.mapper.SampleMapper;
import com.idneo.idneotest.repository.SampleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SampleServiceTest {

    @Mock
    private SampleRepository sampleRepository;

    @Mock
    private SampleMapper sampleMapper;

    @InjectMocks
    private SampleService sampleService;

    @Test
    void registerSample_shouldSaveAndReturnDto() {
        // Given
        UUID patientId = UUID.randomUUID();
        Instant collectedAt = Instant.now();
        SampleRequestDto requestDto = new SampleRequestDto(patientId, collectedAt);
        Sample sample = Sample.builder().patientId(patientId).collectedAt(collectedAt).build();
        Sample savedSample = Sample.builder().id(UUID.randomUUID()).patientId(patientId).collectedAt(collectedAt).status(SampleStatus.REGISTERED).build();
        SampleResponseDto responseDto = new SampleResponseDto(savedSample.getId(), patientId, SampleStatus.REGISTERED, collectedAt, null);

        when(sampleMapper.toEntity(requestDto)).thenReturn(sample);
        when(sampleRepository.save(any(Sample.class))).thenReturn(savedSample);
        when(sampleMapper.toResponseDto(savedSample)).thenReturn(responseDto);

        // When
        SampleResponseDto result = sampleService.registerSample(requestDto);

        // Then
        assertThat(result).isEqualTo(responseDto);
        verify(sampleRepository).save(sample);
        assertThat(sample.getStatus()).isEqualTo(SampleStatus.REGISTERED);
        assertThat(sample.getProcessedAt()).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void getSamples_shouldFilterAndReturnList() {
        // Given
        UUID patientId = UUID.randomUUID();
        SampleStatus status = SampleStatus.REGISTERED;
        Sample sample = Sample.builder().patientId(patientId).status(status).build();
        SampleResponseDto responseDto = new SampleResponseDto(UUID.randomUUID(), patientId, status, Instant.now(), null);

        when(sampleRepository.findAll(any(Specification.class))).thenReturn(List.of(sample));
        when(sampleMapper.toResponseDto(sample)).thenReturn(responseDto);

        // When
        List<SampleResponseDto> result = sampleService.getSamples(status, patientId, null, null);

        // Then
        assertThat(result).hasSize(1).containsExactly(responseDto);
        verify(sampleRepository).findAll(any(Specification.class));
    }

    @Test
    void processSample_shouldUpdateStatusAndReturnDto() {
        // Given
        UUID id = UUID.randomUUID();
        Sample sample = Sample.builder().id(id).status(SampleStatus.REGISTERED).build();
        Sample updatedSample = Sample.builder().id(id).status(SampleStatus.PROCESSED).processedAt(Instant.now()).build();
        SampleResponseDto responseDto = new SampleResponseDto(id, UUID.randomUUID(), SampleStatus.PROCESSED, Instant.now(), updatedSample.getProcessedAt());

        when(sampleRepository.findById(id)).thenReturn(Optional.of(sample));
        when(sampleRepository.save(any(Sample.class))).thenReturn(updatedSample);
        when(sampleMapper.toResponseDto(updatedSample)).thenReturn(responseDto);

        // When
        SampleResponseDto result = sampleService.processSample(id);

        // Then
        assertThat(result).isEqualTo(responseDto);
        assertThat(sample.getStatus()).isEqualTo(SampleStatus.PROCESSED);
        assertThat(sample.getProcessedAt()).isNotNull();
        verify(sampleRepository).save(sample);
    }

    @Test
    void processSample_shouldThrowNotFoundException_whenSampleDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();
        when(sampleRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sampleService.processSample(id))
                .isInstanceOf(SampleNotFoundException.class);
        verify(sampleRepository, never()).save(any());
    }

    @Test
    void processSample_shouldThrowAlreadyProcessedException_whenSampleIsAlreadyProcessed() {
        // Given
        UUID id = UUID.randomUUID();
        Sample sample = Sample.builder().id(id).status(SampleStatus.PROCESSED).build();
        when(sampleRepository.findById(id)).thenReturn(Optional.of(sample));

        // When & Then
        assertThatThrownBy(() -> sampleService.processSample(id))
                .isInstanceOf(SampleAlreadyProcessedException.class);
        verify(sampleRepository, never()).save(any());
    }
}
