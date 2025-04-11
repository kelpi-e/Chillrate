package com.example.serverchillrate.secutiry.controllers;

import com.example.serverchillrate.dto.AuthResponse;
import com.example.serverchillrate.dto.UserDto;
import com.example.serverchillrate.models.User;
import com.example.serverchillrate.secutiry.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/*
endpoint для регистрации и авторизации пользователя
*/
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    /*
    сервис, реализующий регистрацию и авторизацию пользователей
     */

    private final AuthenticationService service;
    /*
    endpoint для регистрации
    */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserDto user){
        return ResponseEntity.ok(service.register(user));
    }
    /*
    endpoint для авторизации
    */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody UserDto user){
        return ResponseEntity.ok(service.authenticate(user));
    }


}
