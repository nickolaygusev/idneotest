package com.idneo.idneotest.repository;

import com.idneo.idneotest.domain.model.BloodSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BloodSampleRepository extends JpaRepository<BloodSample, UUID>, JpaSpecificationExecutor<BloodSample> {
}
