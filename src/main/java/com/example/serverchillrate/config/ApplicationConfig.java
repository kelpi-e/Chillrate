package com.example.serverchillrate.config;

import com.example.serverchillrate.models.ServerData;
import com.example.serverchillrate.models.UserApp;
import com.example.serverchillrate.models.UserTemp;
import com.example.serverchillrate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private  final UserRepository repository;
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> repository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("user not found"));
    }
    @Bean
    public  AuthenticationProvider authenticationProvider(){
        var authProvider=new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    };
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    /*
    Множесство user ожидающих подтверждение почты
    */
    @Bean
    public HashMap<UUID, UserTemp> tempSetUser(){
        return new HashMap<>();
    }
    @Bean
    public ServerData serverData(){
        return ServerData.builder().
                externalHost(System.getenv("SPRING_HOST")).
                externalPort(System.getenv("SPRING_PORT")).build();
    }
}



