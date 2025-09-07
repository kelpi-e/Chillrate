package com.example.serverchillrate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;
@Schema(description = "сущность команды")
@Data
@Builder
public class TeamDto {
    @Schema(description = "id команды,при создании автоматически генерируется, при обновлении необходимо указать")
    Long id;
    @Schema(description = "имя команды",example = "chillteam")
    String name;
    @Schema(description = "список пользователей в этой команде")
    List<UserDto> clients;
}
