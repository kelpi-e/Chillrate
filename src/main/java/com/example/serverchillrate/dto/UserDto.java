package com.example.serverchillrate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "сущность пользователь")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @Schema(description = "id пользвателя, при созданни генерируется автоматически")
    UUID id;
    @Schema(description = "имя пользователя")
    String name;
    @Schema(description = "email пользователя")
    String email;
    @Schema(description = "пароль пользователя")
    String password;
}
