package com.example.serverchillrate.controllers;

import com.example.serverchillrate.ServerChillRateApplication;
import com.example.serverchillrate.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PublicController {
    private final EmailService emailService;

    static final Logger log =
            LoggerFactory.getLogger(ServerChillRateApplication.class);
    @GetMapping("/tests")
    public String getHello(){
        return "Hello from docker";
    }
    @GetMapping("/tests/{email}")
    public @ResponseBody ResponseEntity sendMessage(@PathVariable String email){
        try{
            emailService.SendSimpleMessage(email,"hello","hello");

        }
        catch (MailException exception){
            log.info("MailException: "+exception.getMessage());
            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
