package com.example.serverchillrate.secutiry.jwt;

import com.example.serverchillrate.models.UserTemp;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/*
Фильтр jwt регистрирующий пользователя при наличии и актуальности токена.
Токен передаётся в header с параметрами
key=Authorization
value=Client <token>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private final HashMap<UUID, UserTemp> tempUsers;
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader=request.getHeader("Authorization");


        if(authHeader==null ||!authHeader.startsWith("Client ")&&!authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        try{
        final String jwt=authHeader.substring(7);


        if(SecurityContextHolder.getContext().getAuthentication()==null){
            //  UserDetails userDetails =this.userDetailsService.loadUserByUsername(userEmail);
            if(jwtService.isTokenValid(jwt)){
                var authUser=jwtService.getAuthUser(jwt);
                if(!authUser.isConfirmMail()) {
                    UserDetails checkUser = userDetailsService.loadUserByUsername(authUser.getUuid().toString());
                    if (checkUser == null) {
                        throw new JwtException("user not found");
                    }
                }
                UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(
                        authUser,
                        null,
                        authUser.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        }
        catch (Exception exception){
            throw new JwtException(exception.getMessage());
        }
        filterChain.doFilter(request,response);
    }

}