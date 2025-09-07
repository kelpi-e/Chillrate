package com.example.serverchillrate.secutiry.service.impl;

import com.example.serverchillrate.dto.AuthResponse;
import com.example.serverchillrate.dto.UserMapper;
import com.example.serverchillrate.repository.UserRepository;
import com.example.serverchillrate.repository.AuthorizationDetailsrepository;
import com.example.serverchillrate.secutiry.jwt.JwtService;
import com.example.serverchillrate.secutiry.service.RefreshTokenService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthorizationDetailsrepository authorizationDetailsrepository;
    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if(jwtService.isTokenValid(refreshToken)) {
            var authUser = jwtService.getAuthUser(refreshToken);
            var user= userRepository.findById(authUser.getUuid()).orElseThrow();
            var authorizationDetails= authorizationDetailsrepository.findByUserAppAndDevice(user, authUser.getDevice()).orElseThrow();
            if(authorizationDetails.getSecret().equals(authUser.getSecret())){
                authorizationDetails.setSecret(UUID.randomUUID());
                authorizationDetailsrepository.save(authorizationDetails);
                return AuthResponse.builder()
                        .refreshToken(jwtService.generateRefreshToken(user,true,authorizationDetails))
                        .accessToken(jwtService.generateToken(user))
                        .user(UserMapper.INSTANCE.toDto(user))
                        .build();
            }
        }
        throw new JwtException("refresh token invalid");
    }
}
