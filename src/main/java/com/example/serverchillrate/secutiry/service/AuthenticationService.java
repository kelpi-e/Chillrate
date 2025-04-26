package com.example.serverchillrate.secutiry.service;


import com.example.serverchillrate.dto.AuthResponse;
import com.example.serverchillrate.dto.UserDto;
import com.example.serverchillrate.dto.UserMapper;
import com.example.serverchillrate.secutiry.jwt.JwtService;
import com.example.serverchillrate.secutiry.Role;
import com.example.serverchillrate.models.User;
import com.example.serverchillrate.repository.UserRepository;
import com.example.serverchillrate.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/*
Сервер авторизации
реализуют регистрацию и авторизацию пользователя
*/
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    /*репозиторий пользователей для работы с бд*/
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final HashMap<UUID,User> tempUsers;
    /*
    регистрация нового пользователя
    добавляет нового пользователя с правами доступа USER
    request-запрос на добавления пользователя(Нужно сделать класс UserDto)
    результат:string (нужно сделать класс AuthenticateResponse)
    */
    public AuthResponse register(UserDto request){
        if(repository.findByEmail(request.getEmail()).isEmpty()){
            var user = User.builder()
                    .id(UUID.randomUUID())
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();

            //(поставить переменную среды SPRING_HOST)
            emailService.SendSimpleMessage(request.getEmail(),"Register for chillrate",
                    "localhost:8080/api/v1/auth/confirmMail/"+user.getId().toString());
            tempUsers.put(user.getId(),user);
            var token=jwtService.generateToken(user);
            return AuthResponse.builder().token(token).user(UserMapper.INSTANCE.toDto(user)).build();
        }
        throw  new UsernameNotFoundException("User already have");
    }
    /*
    авторизация пользователя
    request-запрос на авторизацию(Нужно сделать класс UserDto)
    результат:string (нужно сделать класс AuthenticateResponse)
    */
    public AuthResponse authenticate(UserDto request){
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder().token(jwtToken).user(UserMapper.INSTANCE.toDto(user)).build();
    }
    public  void confirmMail(UUID id){
       User user=tempUsers.get(id);
       if(user!=null){
           repository.save(user);

           tempUsers.remove(id);
           return;
       }
        throw new UsernameNotFoundException("user not found");

    }
}