package com.example.serverchillrate.dto;

import com.example.serverchillrate.entity.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {
    TeamMapper INSTANCE= Mappers.getMapper(TeamMapper.class);
    @Mapping(target = "clients",expression = "java(UserMapper.INSTANCE.toListDto(entity.getClients()))")
    TeamDto toDto(Team entity);
    @Mapping(target = "clients",ignore = true)
    Team toEntity(TeamDto dto);
    List<TeamDto> toListDto(List<Team> listEntity);
}
