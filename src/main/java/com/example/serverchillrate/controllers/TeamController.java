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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/team")
@RequiredArgsConstructor
public class TeamController {
    private final TeamClientService service;
    private final CRUDTeam crudTeam;
    @PostMapping("/{teamId}/{emailUser}")
    @ResponseStatus(HttpStatus.OK)
    public TeamDto confirmToTeam(@PathVariable Long teamId, @PathVariable String emailUser){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        return TeamMapper.INSTANCE.toDto( service.addUser(teamId,emailUser,email));
    }
    @DeleteMapping("/{teamId}/{emailUser}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public TeamDto deleteUserToTeam(@PathVariable Long teamId,@PathVariable String emailUser){
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        return TeamMapper.INSTANCE.toDto( service.deleteUser(teamId,emailUser,email));
    }
    @PostMapping()
    public ResponseEntity<TeamDto> createTeam(@RequestBody TeamDto dto){
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(TeamMapper.INSTANCE.toDto(crudTeam.create(dto,email)));
    }
    @PutMapping()
    public ResponseEntity<TeamDto> updateTeam(@RequestBody TeamDto dto){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(TeamMapper.INSTANCE.toDto(crudTeam.update(dto,email)));
    }
    @GetMapping()
    public ResponseEntity<List<TeamDto>> getTeams(){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(TeamMapper.INSTANCE.toListDto(crudTeam.getListTeam(email)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<TeamDto> getTeam(@PathVariable Long id){
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(TeamMapper.INSTANCE.toDto(crudTeam.read(id,email)));
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeam(@PathVariable Long id){
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        crudTeam.delete(id,email);
    }
}
