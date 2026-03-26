package com.idneo.idneotest.service;

import com.idneo.idneotest.domain.exception.SampleAlreadyProcessedException;
import com.idneo.idneotest.domain.exception.SampleNotFoundException;
import com.idneo.idneotest.domain.model.Sample;
import com.idneo.idneotest.domain.model.SampleStatus;
import com.idneo.idneotest.dto.SampleRequestDto;
import com.idneo.idneotest.dto.SampleResponseDto;
import com.idneo.idneotest.mapper.SampleMapper;
import com.idneo.idneotest.repository.SampleRepository;
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
public class SampleService {

    private final SampleRepository sampleRepository;
    private final SampleMapper sampleMapper;

    @Transactional
    public SampleResponseDto registerSample(SampleRequestDto requestDto) {
        Sample sample = sampleMapper.toEntity(requestDto);
        sample.setStatus(SampleStatus.REGISTERED);
        sample.setProcessedAt(null);
        Sample savedSample = sampleRepository.save(sample);
        return sampleMapper.toResponseDto(savedSample);
    }

    @Transactional(readOnly = true)
    public List<SampleResponseDto> getSamples(SampleStatus status, UUID patientId, Instant fromDate, Instant toDate) {
        Specification<Sample> spec = (root, query, criteriaBuilder) -> {
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
    public SampleResponseDto processSample(UUID id) {
        Sample sample = sampleRepository.findById(id)
                .orElseThrow(() -> new SampleNotFoundException("Sample with id " + id + " not found"));

        if (sample.getStatus() == SampleStatus.PROCESSED) {
            throw new SampleAlreadyProcessedException("Sample with id " + id + " is already processed");
        }

        sample.setStatus(SampleStatus.PROCESSED);
        sample.setProcessedAt(Instant.now());

        Sample updatedSample = sampleRepository.save(sample);
        return sampleMapper.toResponseDto(updatedSample);
    }
}
