package com.example.serverchillrate.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TeamDto {
    Long id;
    String name;
    List<UserDto> clients;
}
