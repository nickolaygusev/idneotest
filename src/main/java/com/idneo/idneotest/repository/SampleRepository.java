package com.idneo.idneotest.repository;

import com.idneo.idneotest.domain.model.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SampleRepository extends JpaRepository<Sample, UUID>, JpaSpecificationExecutor<Sample> {
}
