package com.example.serverchillrate.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {
    @GetMapping
    public String getHello(){
        return "Hello from docker";
    }
}
