package com.example.serverchillrate.secutiry.controllers;

import com.example.serverchillrate.ServerChillRateApplication;
import com.example.serverchillrate.dto.AuthResponse;
import com.example.serverchillrate.dto.UserDto;
import com.example.serverchillrate.models.User;
import com.example.serverchillrate.secutiry.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    private final AuthenticationService service;
    Logger logger=LoggerFactory.getLogger(AuthenticationController.class);

    /*
    endpoint для регистрации
    */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserDto user){
        try{
            return ResponseEntity.ok( service.register(user));
        }catch (MailException ex){
            logger.info("Mail exception:"+ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /*
    endpoint для авторизации
    */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody UserDto user){
        return ResponseEntity.ok(service.authenticate(user));
    }
    @GetMapping("/confirmMail/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void confirmMail(@PathVariable UUID id){
        service.confirmMail(id);
    }
}
