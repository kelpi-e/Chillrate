package com.example.serverchillrate.controllers;

import com.example.serverchillrate.dto.RequestInfluxQueryOptions;
import com.example.serverchillrate.dto.TeamDto;
import com.example.serverchillrate.dto.TeamMapper;
import com.example.serverchillrate.entity.InfluxPointApp;
import com.example.serverchillrate.models.AuthUser;
import com.example.serverchillrate.services.CRUDTeam;
import com.example.serverchillrate.services.TeamClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@SecurityRequirement(name = "JWT")
@Tag(name="TeamController",description = "Контроллер управления командами, включающий в себя crud команды и даобавление удаление пользователей в команде и получение данных пользователей в команде")
@RestController
@RequestMapping("/api/v1/team")
@RequiredArgsConstructor
public class TeamController {
    private final TeamClientService service;
    private final CRUDTeam crudTeam;
    @Operation(summary = "Добавление пользователя в команду",description = "добавление пользователяБ ожидающего распределения, в команду")
    @PostMapping("/{teamId}/{userID}")
    @ResponseStatus(HttpStatus.OK)
    public TeamDto confirmToTeam(@Parameter(required = true) @PathVariable Long teamId,
                                 @Parameter(required = true) @PathVariable UUID userID,
                                 @Parameter(description = "Данные получаемые из jwt") @AuthenticationPrincipal AuthUser authUser){
        return TeamMapper.INSTANCE.toDto( service.addUser(teamId,userID,authUser.getUuid()));
    }
    @Operation(summary = "Удаление пользователей из команды")
    @DeleteMapping("/{teamId}/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public TeamDto deleteUserToTeam(@Parameter(required = true) @PathVariable Long teamId,
                                    @Parameter(required = true) @PathVariable UUID userId,
                                    @Parameter(description = "Данные получаемые из jwt") @AuthenticationPrincipal AuthUser authUser){
        return TeamMapper.INSTANCE.toDto( service.deleteUser(teamId,userId,authUser.getUuid()));
    }
    @Operation(summary = "Создание команды")
    @PostMapping()
    public ResponseEntity<TeamDto> createTeam(@Parameter(required = true) @RequestBody TeamDto dto,
                                              @Parameter(description = "Данные получаемые из jwt") @AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(TeamMapper.INSTANCE.toDto(crudTeam.create(dto,authUser.getUuid())));
    }
    @Operation(summary = "Обновление имени команды")
    @PutMapping()
    public ResponseEntity<TeamDto> updateTeam(@Parameter(required = true) @RequestBody TeamDto dto,
                                              @Parameter(description = "Данные получаемые из jwt") @AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(TeamMapper.INSTANCE.toDto(crudTeam.update(dto,authUser.getUuid())));
    }
    @Operation(summary = "Получить список команд")
    @GetMapping()
    public ResponseEntity<List<TeamDto>> getTeams(@Parameter(description = "Данные получаемые из jwt") @AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(TeamMapper.INSTANCE.toListDto(crudTeam.getListTeam(authUser.getUuid())));
    }
    @Operation(summary = "Получить данные команды")
    @GetMapping("/{id}")
    public ResponseEntity<TeamDto> getTeam(@Parameter(required = true) @PathVariable Long id,
                                           @Parameter(description = "Данные получаемые из jwt") @AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(TeamMapper.INSTANCE.toDto(crudTeam.read(id,authUser.getUuid())));
    }
    @Operation(summary = "Удаленние команды")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeam(@Parameter(required = true) @PathVariable Long id,@Parameter(description = "Данные получаемые из jwt") @AuthenticationPrincipal AuthUser authUser){
        crudTeam.delete(id,authUser.getUuid());
    }
    @Operation(summary = "Получить данные члена команды")
    @GetMapping("/{teamId}/{userId}/data/{typeSensor}")
    public ResponseEntity<List<InfluxPointApp>> getUserData(@Parameter(required = true) @PathVariable Long teamId,
                                                            @Parameter(required = true) @PathVariable UUID userId,
                                                            @Parameter(description = "Данные получаемые из jwt") @AuthenticationPrincipal AuthUser authUser,
                                                            @Parameter(required = true) @PathVariable String typeSensor,
                                                            @Parameter(description = "интервал времени,за который берём данные, в отрицательнои формате(пример -15m - за посследние 15 минут)") @RequestParam(required = false,name = "start") String start,
                                                            @Parameter(description = "аггрегирование точек по среднему значению в промежуток премени(пример 5m - 5 минут)")@RequestParam(required = false) String aggregate){

        RequestInfluxQueryOptions options=new RequestInfluxQueryOptions();
        if(start!=null){
            options.setStart(start);
        }
        if(aggregate!=null){
            options.setAggregate(aggregate);
        }
        return ResponseEntity.ok(service.getUserData(teamId,userId,authUser.getUuid(),typeSensor,options));
    }
}
