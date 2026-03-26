package com.idneo.idneotest.service;

import com.idneo.idneotest.domain.exception.BloodSampleAlreadyProcessedException;
import com.idneo.idneotest.domain.exception.BloodSampleNotFoundException;
import com.idneo.idneotest.domain.model.BloodSample;
import com.idneo.idneotest.domain.model.BloodSampleStatus;
import com.idneo.idneotest.dto.BloodSampleRequestDto;
import com.idneo.idneotest.dto.BloodSampleResponseDto;
import com.idneo.idneotest.mapper.BloodSampleMapper;
import com.idneo.idneotest.repository.BloodSampleRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BloodSampleService {

    private final BloodSampleRepository sampleRepository;
    private final BloodSampleMapper sampleMapper;

    @Transactional
    public BloodSampleResponseDto registerSample(BloodSampleRequestDto requestDto) {
        BloodSample sample = sampleMapper.toEntity(requestDto);
        sample.setStatus(BloodSampleStatus.REGISTERED);
        sample.setProcessedAt(null);
        BloodSample savedSample = sampleRepository.save(sample);
        return sampleMapper.toResponseDto(savedSample);
    }

    @Transactional(readOnly = true)
    public List<BloodSampleResponseDto> getSamples(BloodSampleStatus status, UUID patientId, Instant fromDate, Instant toDate) {
        Specification<BloodSample> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (patientId != null) {
                predicates.add(criteriaBuilder.equal(root.get("patientId"), patientId));
            }
            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("collectedAt"), fromDate));
            }
            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("collectedAt"), toDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return sampleRepository.findAll(spec).stream()
                .map(sampleMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public BloodSampleResponseDto processSample(UUID id) {
        BloodSample sample = sampleRepository.findById(id)
                .orElseThrow(() -> new BloodSampleNotFoundException("BloodSample with id " + id + " not found"));

        if (sample.getStatus() == BloodSampleStatus.PROCESSED) {
            throw new BloodSampleAlreadyProcessedException("BloodSample with id " + id + " is already processed");
        }

        sample.setStatus(BloodSampleStatus.PROCESSED);
        sample.setProcessedAt(Instant.now());

        BloodSample updatedSample = sampleRepository.save(sample);
        return sampleMapper.toResponseDto(updatedSample);
    }
}
