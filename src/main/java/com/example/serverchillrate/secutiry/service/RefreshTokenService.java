package com.example.serverchillrate.secutiry.service;

import com.example.serverchillrate.dto.AuthResponse;
import com.example.serverchillrate.models.AuthUser;
import com.nimbusds.oauth2.sdk.dpop.verifiers.AccessTokenValidationException;

public interface RefreshTokenService {
    AuthResponse refreshToken(String refreshToken);
}
