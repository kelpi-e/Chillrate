package com.example.serverchillrate.controllers;

import com.example.serverchillrate.dto.PointDto;
import com.example.serverchillrate.dto.PointDtoMapper;
import com.example.serverchillrate.dto.UserDto;
import com.example.serverchillrate.dto.UserMapper;

import com.example.serverchillrate.entity.InfluxPointApp;
import com.example.serverchillrate.models.AuthUser;

import com.example.serverchillrate.services.AdminService;
import com.example.serverchillrate.services.InfluxDBService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    @GetMapping("/url")
    public ResponseEntity<String> getUrl(@AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(adminService.getUrl(authUser.getUuid()));
    }
    @GetMapping("/waitUsers")
    public ResponseEntity<List<UserDto>> getWaitUsers(@AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(UserMapper.INSTANCE.toListDto(adminService.getUsersWait(authUser.getUuid())));
    }

}
