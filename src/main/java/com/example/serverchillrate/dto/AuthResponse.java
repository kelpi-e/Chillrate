package com.example.serverchillrate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Schema(description = "Ответ при авторизации")
@Data
@Builder
public class AuthResponse {
    @Schema(description = "Быстрый токен авторизации")
    String accessToken;
    @Schema(description = "Токен обновления")
    String refreshToken;
    UserDto user;
}
