package com.example.serverchillrate.config;

import com.example.serverchillrate.dto.ResponseExceptionApp;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ConfigException {
    Logger logger= LoggerFactory.getLogger(ConfigException.class);
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseExceptionApp> usernameNotFoundException(UsernameNotFoundException exception){
        //logger.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseExceptionApp.builder().message(exception.getMessage()).build());
    }
    @ExceptionHandler(MailException.class)
    public ResponseEntity<ResponseExceptionApp> mailException(MailException mailException){
        //logger.error(mailException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseExceptionApp.builder().message(mailException.getMessage()).build());
    }
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseExceptionApp> noSuchFieldError(NoSuchElementException exception){
        logger.error(exception.getMessage());
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseExceptionApp.builder().message(exception.getMessage()).build());
    }
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ResponseExceptionApp> jwtException(JwtException exception){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseExceptionApp.builder().message(exception.getMessage()).build());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseExceptionApp> exception(Exception exception){
        logger.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseExceptionApp.builder().message(exception.getMessage()).build());
    }
}
