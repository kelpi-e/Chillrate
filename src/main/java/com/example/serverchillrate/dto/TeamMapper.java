package com.example.serverchillrate.dto;

import com.example.serverchillrate.models.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {
    TeamMapper INSTANCE= Mappers.getMapper(TeamMapper.class);
    @Mapping(target = "clients",expression = "java(entity.clientsToListId())")
    TeamDto toDto(Team entity);
    @Mapping(target = "clients",ignore = true)
    Team toEntity(TeamDto dto);
}
