package com.example.serverchillrate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/*
тестовый endpoint возращает строку
 */
@RestController()
@RequestMapping("/api/v1/test")
public class TestController {
    @GetMapping()
    public String getTest(){
        return "Hello";
    }
}
