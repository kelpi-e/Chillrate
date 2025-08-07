package com.example.serverchillrate.secutiry.service.impl;


import com.example.serverchillrate.dto.AuthResponse;
import com.example.serverchillrate.dto.UserDto;
import com.example.serverchillrate.dto.UserMapper;
import com.example.serverchillrate.models.ServerData;
import com.example.serverchillrate.entity.UserApp;
import com.example.serverchillrate.models.UserTemp;
import com.example.serverchillrate.secutiry.jwt.JwtService;
import com.example.serverchillrate.secutiry.Role;
import com.example.serverchillrate.repository.UserRepository;
import com.example.serverchillrate.secutiry.service.AuthService;
import com.example.serverchillrate.secutiry.service.UdpServiceSecure;
import com.example.serverchillrate.services.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/*
Сервер авторизации
реализуют регистрацию и авторизацию пользователя
*/
@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final HashMap<UUID, UserTemp> tempUsers;
    private final ServerData serverData;
    private final UdpServiceSecure udpServiceSecure;
    private final HashMap<UUID, HashSet<UserTemp>> adminUsersTemp;
    /*
    регистрация нового пользователя
    добавляет нового пользователя с правами доступа USER
    request-запрос на добавления пользователя(Нужно сделать класс UserDto)
    результат:string (нужно сделать класс AuthenticateResponse)
    */
    public AuthResponse register(UserDto request,Role role) throws UsernameNotFoundException {
            if (repository.findByEmail(request.getEmail()).isEmpty()) {
                var user = UserApp.builder()
                        .id(UUID.randomUUID())
                        .email(request.getEmail())
                        .name(request.getName())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(role)
                        .build();

                emailService.SendSimpleMessage(request.getEmail(), "Register for chillrate",
                        "http://" + serverData.getExternalHost() + ":" + serverData.getExternalPort() + "/api/v1/auth/confirmMail/" + user.getId().toString());
                tempUsers.put(user.getId(), new UserTemp(user, new Date()));
                var token = jwtService.generateToken(user);
                udpServiceSecure.addJwtToClient(token, user);
                return AuthResponse.builder().token(token).user(UserMapper.INSTANCE.toDto(user)).build();
            }

        throw  new UsernameNotFoundException("User already have");
    }
    /*
    авторизация пользователя
    request-запрос на авторизацию(Нужно сделать класс UserDto)
    результат:string (нужно сделать класс AuthenticateResponse)
    */
    public AuthResponse authenticate(UserDto request)throws UsernameNotFoundException{
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(()->new UsernameNotFoundException("not found user"));
        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new UsernameNotFoundException("password not equals");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var jwtToken = jwtService.generateToken(user);
        udpServiceSecure.addClientData(user,jwtToken);
        udpServiceSecure.addJwtToClient(jwtToken,user);
        if(user.getRole()==Role.ADMIN &&!adminUsersTemp.containsKey(user.getId())){
            adminUsersTemp.put(user.getId(),new HashSet<>());
        }
        return AuthResponse.builder().token(jwtToken).user(UserMapper.INSTANCE.toDto(user)).build();
    }
    /*
    функция подтверждения почты
    если в tempUser присутствует пользователь
    с таким именем добавляет его в бд
     */
    public  void confirmMail(UUID id)throws UsernameNotFoundException{
       UserTemp userTemp=tempUsers.get(id);
       if(userTemp!=null){
           repository.save(userTemp.getUser());
           udpServiceSecure.addClientData(userTemp.getUser(),null);
           tempUsers.remove(id);
           if(userTemp.getUser().getRole()==Role.ADMIN){
               adminUsersTemp.put(userTemp.getUser().getId(),new HashSet<>());
           }
           return;
       }
        throw new UsernameNotFoundException("user not found");

    }

}