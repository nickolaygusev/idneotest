package com.idneo.idneotest.mapper;

import com.idneo.idneotest.domain.model.BloodSample;
import com.idneo.idneotest.dto.BloodSampleRequestDto;
import com.idneo.idneotest.dto.BloodSampleResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BloodSampleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "processedAt", ignore = true)
    BloodSample toEntity(BloodSampleRequestDto requestDto);

    BloodSampleResponseDto toResponseDto(BloodSample entity);
}
