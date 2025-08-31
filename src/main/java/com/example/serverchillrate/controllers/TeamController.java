package com.example.serverchillrate.controllers;

import com.example.serverchillrate.dto.RequestInfluxQueryOptions;
import com.example.serverchillrate.dto.TeamDto;
import com.example.serverchillrate.dto.TeamMapper;
import com.example.serverchillrate.entity.InfluxPointApp;
import com.example.serverchillrate.models.AuthUser;
import com.example.serverchillrate.services.CRUDTeam;
import com.example.serverchillrate.services.TeamClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PostMapping("/{teamId}/{userID}")
    @ResponseStatus(HttpStatus.OK)
    public TeamDto confirmToTeam(@PathVariable Long teamId,
                                 @PathVariable UUID userID,
                                 @AuthenticationPrincipal AuthUser authUser){
        return TeamMapper.INSTANCE.toDto( service.addUser(teamId,userID,authUser.getUuid()));
    }
    @DeleteMapping("/{teamId}/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public TeamDto deleteUserToTeam(@PathVariable Long teamId,
                                    @PathVariable UUID userId,
                                    @AuthenticationPrincipal AuthUser authUser){
        return TeamMapper.INSTANCE.toDto( service.deleteUser(teamId,userId,authUser.getUuid()));
    }
    @PostMapping()
    public ResponseEntity<TeamDto> createTeam(@RequestBody TeamDto dto,
                                              @AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(TeamMapper.INSTANCE.toDto(crudTeam.create(dto,authUser.getUuid())));
    }
    @PutMapping()
    public ResponseEntity<TeamDto> updateTeam(@RequestBody TeamDto dto,
                                              @AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(TeamMapper.INSTANCE.toDto(crudTeam.update(dto,authUser.getUuid())));
    }
    @GetMapping()
    public ResponseEntity<List<TeamDto>> getTeams(@AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(TeamMapper.INSTANCE.toListDto(crudTeam.getListTeam(authUser.getUuid())));
    }
    @GetMapping("/{id}")
    public ResponseEntity<TeamDto> getTeam(@PathVariable Long id,
                                           @AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(TeamMapper.INSTANCE.toDto(crudTeam.read(id,authUser.getUuid())));
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeam(@PathVariable Long id,@AuthenticationPrincipal AuthUser authUser){
        crudTeam.delete(id,authUser.getUuid());
    }
    @GetMapping("/{teamId}/{userId}/data/{typeSensor}")
    public ResponseEntity<List<InfluxPointApp>> getUserData(@PathVariable Long teamId,
                                                            @PathVariable UUID userId,
                                                            @AuthenticationPrincipal AuthUser authUser,
                                                            @PathVariable String typeSensor,
                                                            @RequestBody(required = false)RequestInfluxQueryOptions options){
        if(options==null){
            options=new RequestInfluxQueryOptions();
        }
        return ResponseEntity.ok(service.getUserData(teamId,userId,authUser.getUuid(),typeSensor,options));
    }
}
