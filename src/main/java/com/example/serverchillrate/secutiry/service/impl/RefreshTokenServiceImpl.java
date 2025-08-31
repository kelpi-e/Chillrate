package com.example.serverchillrate.secutiry.service.impl;

import com.example.serverchillrate.dto.AuthResponse;
import com.example.serverchillrate.dto.UserMapper;
import com.example.serverchillrate.models.AuthUser;
import com.example.serverchillrate.repository.UserRepository;
import com.example.serverchillrate.repository.UserSecurityDetailsRepository;
import com.example.serverchillrate.secutiry.jwt.JwtService;
import com.example.serverchillrate.secutiry.service.RefreshTokenService;
import com.nimbusds.oauth2.sdk.dpop.verifiers.AccessTokenValidationException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserSecurityDetailsRepository securityDetailsRepository;
    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if(jwtService.isTokenValid(refreshToken)) {
            var authUser = jwtService.getAuthUser(refreshToken);
            var user= userRepository.findById(authUser.getUuid()).orElseThrow();
            var securityDetails=securityDetailsRepository.findByUserAppAndDevice(user, authUser.getDevice()).orElseThrow();
            if(securityDetails.getSecret().equals(authUser.getSecret())){
                securityDetails.setSecret(UUID.randomUUID());
                securityDetailsRepository.save(securityDetails);
                return AuthResponse.builder()
                        .refreshToken(jwtService.generateRefreshToken(user,true,securityDetails))
                        .accessToken(jwtService.generateToken(user))
                        .user(UserMapper.INSTANCE.toDto(user))
                        .build();
            }
        }
        throw new UsernameNotFoundException("token invalid");
    }
}
