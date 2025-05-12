package com.example.serverchillrate.secutiry.service;

import com.example.serverchillrate.dto.AuthResponse;
import com.example.serverchillrate.dto.UserDto;
import com.example.serverchillrate.secutiry.Role;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;
/*
сервис для авторизации регисстрации и подтверждения почты
 */
public interface AuthService {
    /*
    request необходимы 2 переменные email и password
    Role назначается в зависимости от endpoint
    выбрасывает ошибку если пользователь уже есть в бд
     */
     AuthResponse register(UserDto request, Role role)throws UsernameNotFoundException;
     /*
     аналогичные требования для request
     выбрасывает ошибку если пользователя нет в бд с сообщение об этом
     выбрасывает ошибку если пароли не совпадают  с сообщением об этом
      */
    AuthResponse authenticate(UserDto request)throws UsernameNotFoundException;
    /*
    подтверждение почты
    в случае если id не найдет выбрасывает ошибку
     */
    void confirmMail(UUID id) throws UsernameNotFoundException;
}
