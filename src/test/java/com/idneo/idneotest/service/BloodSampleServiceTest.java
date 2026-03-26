package com.idneo.idneotest.service;

import com.idneo.idneotest.domain.exception.BloodSampleAlreadyProcessedException;
import com.idneo.idneotest.domain.exception.BloodSampleNotFoundException;
import com.idneo.idneotest.domain.model.BloodSample;
import com.idneo.idneotest.domain.model.BloodSampleStatus;
import com.idneo.idneotest.dto.BloodSampleRequestDto;
import com.idneo.idneotest.dto.BloodSampleResponseDto;
import com.idneo.idneotest.mapper.BloodSampleMapper;
import com.idneo.idneotest.repository.BloodSampleRepository;
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
class BloodSampleServiceTest {

    @Mock
    private BloodSampleRepository sampleRepository;

    @Mock
    private BloodSampleMapper sampleMapper;

    @InjectMocks
    private BloodSampleService sampleService;

    @Test
    void registerSampleShouldSaveAndReturnDto() {
        // Given
        UUID patientId = UUID.randomUUID();
        Instant collectedAt = Instant.now();
        BloodSampleRequestDto requestDto = new BloodSampleRequestDto(patientId, collectedAt);
        BloodSample sample = BloodSample.builder().patientId(patientId).collectedAt(collectedAt).build();
        BloodSample savedSample = BloodSample.builder().id(UUID.randomUUID()).patientId(patientId).collectedAt(collectedAt).status(BloodSampleStatus.REGISTERED).build();
        BloodSampleResponseDto responseDto = new BloodSampleResponseDto(savedSample.getId(), patientId, BloodSampleStatus.REGISTERED, collectedAt, null);

        when(sampleMapper.toEntity(requestDto)).thenReturn(sample);
        when(sampleRepository.save(any(BloodSample.class))).thenReturn(savedSample);
        when(sampleMapper.toResponseDto(savedSample)).thenReturn(responseDto);

        // When
        BloodSampleResponseDto result = sampleService.registerSample(requestDto);

        // Then
        assertThat(result).isEqualTo(responseDto);
        verify(sampleRepository).save(sample);
        assertThat(sample.getStatus()).isEqualTo(BloodSampleStatus.REGISTERED);
        assertThat(sample.getProcessedAt()).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void getSamplesShouldFilterAndReturnList() {
        // Given
        UUID patientId = UUID.randomUUID();
        BloodSampleStatus status = BloodSampleStatus.REGISTERED;
        BloodSample sample = BloodSample.builder().patientId(patientId).status(status).build();
        BloodSampleResponseDto responseDto = new BloodSampleResponseDto(UUID.randomUUID(), patientId, status, Instant.now(), null);

        when(sampleRepository.findAll(any(Specification.class))).thenReturn(List.of(sample));
        when(sampleMapper.toResponseDto(sample)).thenReturn(responseDto);

        // When
        List<BloodSampleResponseDto> result = sampleService.getSamples(status, patientId, null, null);

        // Then
        assertThat(result).hasSize(1).containsExactly(responseDto);
        verify(sampleRepository).findAll(any(Specification.class));
    }

    @Test
    void processSampleShouldUpdateStatusAndReturnDto() {
        // Given
        UUID id = UUID.randomUUID();
        BloodSample sample = BloodSample.builder().id(id).status(BloodSampleStatus.REGISTERED).build();
        BloodSample updatedSample = BloodSample.builder().id(id).status(BloodSampleStatus.PROCESSED).processedAt(Instant.now()).build();
        BloodSampleResponseDto responseDto = new BloodSampleResponseDto(id, UUID.randomUUID(), BloodSampleStatus.PROCESSED, Instant.now(), updatedSample.getProcessedAt());

        when(sampleRepository.findById(id)).thenReturn(Optional.of(sample));
        when(sampleRepository.save(any(BloodSample.class))).thenReturn(updatedSample);
        when(sampleMapper.toResponseDto(updatedSample)).thenReturn(responseDto);

        // When
        BloodSampleResponseDto result = sampleService.processSample(id);

        // Then
        assertThat(result).isEqualTo(responseDto);
        assertThat(sample.getStatus()).isEqualTo(BloodSampleStatus.PROCESSED);
        assertThat(sample.getProcessedAt()).isNotNull();
        verify(sampleRepository).save(sample);
    }

    @Test
    void processSampleShouldThrowNotFoundExceptionWhenSampleDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();
        when(sampleRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sampleService.processSample(id))
                .isInstanceOf(BloodSampleNotFoundException.class);
        verify(sampleRepository, never()).save(any());
    }

    @Test
    void processSampleShouldThrowAlreadyProcessedExceptionWhenSampleIsAlreadyProcessed() {
        // Given
        UUID id = UUID.randomUUID();
        BloodSample sample = BloodSample.builder().id(id).status(BloodSampleStatus.PROCESSED).build();
        when(sampleRepository.findById(id)).thenReturn(Optional.of(sample));

        // When & Then
        assertThatThrownBy(() -> sampleService.processSample(id))
                .isInstanceOf(BloodSampleAlreadyProcessedException.class);
        verify(sampleRepository, never()).save(any());
    }
}
