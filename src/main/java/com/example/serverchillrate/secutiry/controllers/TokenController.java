package com.example.serverchillrate.secutiry.controllers;

import com.example.serverchillrate.dto.AuthResponse;
import com.example.serverchillrate.models.AuthUser;
import com.example.serverchillrate.secutiry.service.RefreshTokenService;
import com.nimbusds.oauth2.sdk.dpop.verifiers.AccessTokenValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/token")
@RequiredArgsConstructor
public class TokenController {
    private final RefreshTokenService refreshTokenService;
    @PostMapping()
    public ResponseEntity<AuthResponse> updateRefreshToken(@RequestBody String token)  {
        return ResponseEntity.ok(refreshTokenService.refreshToken(token));
    }
}
