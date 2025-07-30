package com.example.serverchillrate.dto;

import com.example.serverchillrate.entity.UserData;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDataMapper {
    UserDataMapper INSTANCE= Mappers.getMapper(UserDataMapper.class);
    UserDataDto toDto(UserData entity);
}
