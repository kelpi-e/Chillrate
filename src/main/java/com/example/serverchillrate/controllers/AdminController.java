package com.example.serverchillrate.controllers;

import com.example.serverchillrate.dto.TeamDto;
import com.example.serverchillrate.dto.TeamMapper;
import com.example.serverchillrate.services.CRUDTeam;
import com.example.serverchillrate.services.TeamClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final TeamClientService service;
    private final CRUDTeam crudTeam;
    @GetMapping("/team/{teamId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public TeamDto confirmToTeam(@PathVariable Long teamId, @PathVariable UUID userId){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        return TeamMapper.INSTANCE.toDto( service.addUser(teamId,userId,email));
    }
    @DeleteMapping("/team/{teamId}/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public TeamDto deleteUserToTeam(@PathVariable Long teamId,@PathVariable UUID userId){
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        return TeamMapper.INSTANCE.toDto( service.deleteUser(teamId,userId,email));
    }
    @PostMapping("/team")
    public ResponseEntity<TeamDto> createTeam(@RequestBody TeamDto dto){
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(TeamMapper.INSTANCE.toDto(crudTeam.create(dto,email)));
    }
    @PutMapping("/team")
    public ResponseEntity<TeamDto> updateTeam(@RequestBody TeamDto dto){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(TeamMapper.INSTANCE.toDto(crudTeam.update(dto,email)));
    }
    @GetMapping("/team/{id}")
    public ResponseEntity<TeamDto> getTeam(@PathVariable Long id){
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(TeamMapper.INSTANCE.toDto(crudTeam.read(id,email)));
    }
    @DeleteMapping("/team/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeam(@PathVariable Long id){
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        crudTeam.delete(id,email);
    }
}
