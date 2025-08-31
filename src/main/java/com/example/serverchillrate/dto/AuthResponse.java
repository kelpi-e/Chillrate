package com.example.serverchillrate.dto;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class AuthResponse {
    String accessToken;
    String refreshToken;
    UserDto user;
}
