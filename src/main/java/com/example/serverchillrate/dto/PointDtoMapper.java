package com.example.serverchillrate.dto;

import com.example.serverchillrate.entity.InfluxPointApp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PointDtoMapper {
    PointDtoMapper INSTANCE= Mappers.getMapper(PointDtoMapper.class);
    @Mapping(target = "time",expression = "java(entity.toLocalDateTime())")
    PointDto toDto(InfluxPointApp entity);
    List<PointDto> toListDto(List<InfluxPointApp> listEntity);
    @Mapping(target = "time",expression = "java(dto.timeToInstant())")
    InfluxPointApp toEntity(PointDto dto);
    List<InfluxPointApp> toListEntity(List<PointDto> listDto);
}
