package com.example.serverchillrate.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/*
тестовый endpoint возращает строку
 */
@Tag(name = "тестовый контроллер для jwt")
@RestController()
@RequestMapping("/api/v1/test")
@SecurityRequirement(name = "JWT")
public class TestController {
    @GetMapping()
    public String getTest(){
        return "Hello";
    }
}
