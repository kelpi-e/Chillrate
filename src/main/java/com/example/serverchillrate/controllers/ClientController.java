package com.example.serverchillrate.controllers;

import com.example.serverchillrate.services.TeamClientService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/client")
public class ClientController {
    private final TeamClientService service;
    @GetMapping("/{adminId}")
    @ResponseStatus(HttpStatus.OK)
    public void addToWait(@PathVariable UUID adminId){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        service.addUserToWait(email,adminId);
    }

}
