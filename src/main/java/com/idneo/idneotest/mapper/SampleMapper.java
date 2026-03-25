package com.idneo.idneotest.mapper;

import com.idneo.idneotest.domain.model.Sample;
import com.idneo.idneotest.dto.SampleRequestDto;
import com.idneo.idneotest.dto.SampleResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SampleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "processedAt", ignore = true)
    Sample toEntity(SampleRequestDto requestDto);

    SampleResponseDto toResponseDto(Sample entity);
}
