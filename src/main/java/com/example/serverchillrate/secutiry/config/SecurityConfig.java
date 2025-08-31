package com.example.serverchillrate.secutiry.config;

import com.example.serverchillrate.secutiry.Role;
import com.example.serverchillrate.secutiry.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/*
конфигурация для security
указаваем к каким ресурсам какая авторизация нужна
устанавливая фильтр jwt токена до основного фильтра
*/
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(customer->{
                            customer.requestMatchers("/v3/**").permitAll();
                            customer.requestMatchers("/swagger-ui/**").permitAll();
                            customer.requestMatchers("/api/v1/auth/**").permitAll();
                            customer.requestMatchers("/api/v1/token").permitAll();
                            customer.requestMatchers("/tests/**").permitAll();
                            customer.requestMatchers("/api/v1/team/**").hasAuthority(Role.ADMIN.name());
                            customer.requestMatchers("/api/v1/admin/**").hasAuthority(Role.ADMIN.name());
                            customer.anyRequest().authenticated();
                        }
                )
                .cors(httpSecurityCorsConfigurer -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Arrays.asList("*"));
                    configuration.setAllowedMethods(Arrays.asList("*"));
                    configuration.setAllowedHeaders(Arrays.asList("*"));
                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", configuration);
                    httpSecurityCorsConfigurer.configurationSource(source);
                })
                .sessionManagement(customer->{
                    customer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}