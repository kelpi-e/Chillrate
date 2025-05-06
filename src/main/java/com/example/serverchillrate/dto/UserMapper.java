package com.example.serverchillrate.dto;

import com.example.serverchillrate.models.UserApp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper INSTANCE= Mappers.getMapper(UserMapper.class);
    @Mapping(target = "password",ignore = true)
    UserDto toDto(UserApp entity);
    UserApp toEntity(UserDto dto);
}
