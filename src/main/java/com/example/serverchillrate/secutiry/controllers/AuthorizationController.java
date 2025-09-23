package com.example.serverchillrate.secutiry.controllers;

import com.example.serverchillrate.dto.AuthResponse;
import com.example.serverchillrate.dto.UserDto;
import com.example.serverchillrate.models.AuthUser;
import com.example.serverchillrate.secutiry.Role;
import com.example.serverchillrate.secutiry.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/*
endpoint для регистрации и авторизации пользователя
*/
@Tag(name = "AuthorizationController",description = "Контроллер для авторизации")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthorizationController {
    /*
    сервис, реализующий регистрацию и авторизацию пользователей
     */

    private final AuthService service;
    Logger logger=LoggerFactory.getLogger(AuthorizationController.class);

    /*
    endpoint для регистрации
    */
    @Operation(description = "регистрация клиента")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Parameter(required = true) @RequestBody UserDto user,
                                                 @Parameter(required = true,description = "устройство с которого регистрируется пользователь") @RequestHeader("User-Agent") String device) throws MessagingException {
        return ResponseEntity.ok( service.register(user, Role.USER,device));
    }
    /*
    endpoint для авторизации
    */
    @Operation(description = "авторизация пользователя")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authorization(@Parameter(required = true) @RequestBody UserDto user,
                                                      @Parameter(required = true,description = "устройство с которого регистрируется пользователь")  @RequestHeader("User-Agent") String device) {
        logger.info(device);
        return ResponseEntity.ok(service.authorization(user,device));
    }
    @Operation(description = "подтверждение почты")
    @GetMapping("/confirmMail/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void confirmMail(@Parameter(required = true) @PathVariable UUID id){
        service.confirmMail(id);
    }
    @Operation(description = "регистрация админа")
    @PostMapping("/regAdmin")
    public ResponseEntity<AuthResponse> regAdmin(@Parameter(required = true) @RequestBody UserDto user,
                                                 @Parameter(required = true,description = "устройство с которого регистрируется пользователь") @RequestHeader("User-Agent") String device) throws MessagingException {
        return ResponseEntity.ok( service.register(user, Role.ADMIN,device));

    }
    @Operation(description = "выход из аккаунта")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal AuthUser authUser){
        service.logout(authUser.getUuid());
    }
}
