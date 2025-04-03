package com.example.serverchillrate.secutiry.service;


import com.example.serverchillrate.secutiry.jwt.JwtService;
import com.example.serverchillrate.secutiry.Role;
import com.example.serverchillrate.models.User;
import com.example.serverchillrate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
/*
Сервер авторизации
реализуюет регистрацию и авторизацию пользователя
*/
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    /*
    регистрация нового пользователя
    добавляет нового пользователя с правами доступа USER
    request-запрос на добавления пользователя(Нужно сделать класс UserDto)
    результат:string (нужно сделать класс AuthenticateResponse)
    */
    public String register(User request){
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        if(repository.findByUsername(request.getUsername()).isEmpty()){
            repository.save(user);
            var jwtToken = jwtService.generateToken(user);
            return jwtToken;
        }
        throw  new UsernameNotFoundException("User already have");
    }
    /*
    автооризация пользователя
    request-запрос на авторизация(Нужно сделать класс UserDto)
    результат:string (нужно сделать класс AuthenticateResponse)
    */
    public String authenticate(User request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return jwtToken;
    }
}