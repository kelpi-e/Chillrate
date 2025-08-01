package com.example.serverchillrate.controllers;

import com.example.serverchillrate.dto.UserDataDto;
import com.example.serverchillrate.dto.UserDataMapper;
import com.example.serverchillrate.dto.UserDto;
import com.example.serverchillrate.dto.UserMapper;
import com.example.serverchillrate.entity.UserData;
import com.example.serverchillrate.models.ServerData;
import com.example.serverchillrate.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    @GetMapping("/url")
    public ResponseEntity<String> getUrl(){
        var email= SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(adminService.getUrl(email));
    }
    @GetMapping("/waitUsers")
    public ResponseEntity<List<UserDto>> getWaitUsers(){
        var email= SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(UserMapper.INSTANCE.toListDto(adminService.getUsersWait(email)));
    }
    @GetMapping("/{teamID}/{userEmail}")
    public ResponseEntity<List<UserDataDto>> getUserData(@PathVariable Long teamID,@PathVariable String userEmail){
        var email= SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(UserDataMapper.INSTANCE.toListDto(adminService.getUserDataByEmail(teamID,userEmail,email)));
    }

}
