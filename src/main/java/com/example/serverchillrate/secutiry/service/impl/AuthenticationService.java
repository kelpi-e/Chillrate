package com.example.serverchillrate.secutiry.service.impl;


import com.example.serverchillrate.dto.AuthResponse;
import com.example.serverchillrate.dto.UserDto;
import com.example.serverchillrate.dto.UserMapper;
import com.example.serverchillrate.entity.UserRole;
import com.example.serverchillrate.entity.AuthorizationDetails;
import com.example.serverchillrate.models.ServerData;
import com.example.serverchillrate.entity.UserApp;
import com.example.serverchillrate.models.UserTemp;
import com.example.serverchillrate.repository.AuthorizationDetailsrepository;
import com.example.serverchillrate.secutiry.jwt.JwtService;
import com.example.serverchillrate.secutiry.Role;
import com.example.serverchillrate.repository.UserRepository;
import com.example.serverchillrate.secutiry.service.AuthService;
import com.example.serverchillrate.services.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final AuthorizationDetailsrepository authorizationDetailsrepository;
    /*
    регистрация нового пользователя
    добавляет нового пользователя с правами доступа USER
    request-запрос на добавления пользователя(Нужно сделать класс UserDto)
    результат:string (нужно сделать класс AuthenticateResponse)
    */
    public AuthResponse register(UserDto request,Role role,String device) throws UsernameNotFoundException {
            if (repository.findByEmail(request.getEmail()).isEmpty()) {
                var user = UserApp.builder()
                        .id(UUID.randomUUID())
                        .email(request.getEmail())
                        .name(request.getName())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(UserRole.createByEnumRole(role))
                        .adminToken(role.equals(Role.ADMIN)?UUID.randomUUID():null)
                        .build();

                AuthorizationDetails authorizationDetails= AuthorizationDetails.builder()
                        .secret(UUID.randomUUID())
                        .device(device)
                        .userApp(user)
                        .authorizationTime(LocalDateTime.now())
                        .build();
                var refreshToken=jwtService.generateRefreshToken(user,false,authorizationDetails);
                user.setAuthorizationDetails(List.of(authorizationDetails));
                var accessToken = jwtService.generateToken(user);

                emailService.SendSimpleMessage(request.getEmail(), "Register for chillrate",
                        "http://" + serverData.getExternalHost() + ":" + serverData.getExternalPort() + "/api/v1/auth/confirmMail/" + user.getId().toString());
                tempUsers.put(user.getId(), new UserTemp(user, new Date()));


                return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).user(UserMapper.INSTANCE.toDto(user,true)).build();
            }

        throw  new UsernameNotFoundException("User already have");
    }
    /*
    авторизация пользотеля
    request-запрос на авторизацию(Нужно сделать класс UserDto)
    результат:string (нужно сделать класс AuthenticateResponse)
    */
    public AuthResponse authorization(UserDto request,String device)throws UsernameNotFoundException{
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(()->new UsernameNotFoundException("not found user"));
        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new UsernameNotFoundException("password not equals");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getId().toString(),
                        request.getPassword()
                )
        );
        AuthorizationDetails authorizationDetails;
        var oldDetails=user.getAuthorizationDetails().stream().filter(c->c.getDevice().equals(device)).findAny();
        if(oldDetails.isPresent()){
            oldDetails.get().setAuthorizationTime(LocalDateTime.now());
            oldDetails.get().setSecret(UUID.randomUUID());
            authorizationDetails=oldDetails.get();
        }
        else{
            authorizationDetails= AuthorizationDetails.builder().userApp(user).authorizationTime(LocalDateTime.now()).device(device).secret(UUID.randomUUID()).build();
            user.getAuthorizationDetails().add(authorizationDetails);
        }
        repository.save(user);
        var refreshToken= jwtService.generateRefreshToken(user,true,authorizationDetails);
        var accessToken = jwtService.generateToken(user,true);
        return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).user(UserMapper.INSTANCE.toDto(user)).build();
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
           tempUsers.remove(id);
           return;

       }
        throw new UsernameNotFoundException("user not found");

    }
    @Override
    public void logout(UUID id) {
        var user=repository.findById(id).orElseThrow();
        user.getAuthorizationDetails().clear();
        repository.save(user);
    }

}