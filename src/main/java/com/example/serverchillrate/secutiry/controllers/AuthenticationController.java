package com.example.serverchillrate.secutiry.controllers;

import com.example.serverchillrate.dto.AuthResponse;
import com.example.serverchillrate.dto.UserDto;
import com.example.serverchillrate.secutiry.Role;
import com.example.serverchillrate.secutiry.service.AuthService;
import com.example.serverchillrate.secutiry.service.impl.AuthenticationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    private final AuthService service;
    Logger logger=LoggerFactory.getLogger(AuthenticationController.class);

    /*
    endpoint для регистрации
    */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserDto user,
                                                 @RequestHeader("User-Agent") String device) throws MessagingException {
        return ResponseEntity.ok( service.register(user, Role.USER,device));
    }
    /*
    endpoint для авторизации
    */

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody UserDto user,
                                                     @RequestHeader("User-Agent") String device) {
        logger.info(device);
        return ResponseEntity.ok(service.authenticate(user,device));
    }
    @GetMapping("/confirmMail/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void confirmMail(@PathVariable UUID id){
        service.confirmMail(id);
    }

    @PostMapping("/regAdmin")
    public ResponseEntity<AuthResponse> regAdmin(@RequestBody UserDto user,
                                                 @RequestHeader("User-Agent") String device) throws MessagingException {
        return ResponseEntity.ok( service.register(user, Role.ADMIN,device));

    }
}
