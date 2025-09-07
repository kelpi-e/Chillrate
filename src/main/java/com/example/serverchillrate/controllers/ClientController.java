package com.example.serverchillrate.controllers;

import com.example.serverchillrate.dto.ListInfluxPointDto;
import com.example.serverchillrate.dto.PointDto;
import com.example.serverchillrate.dto.PointDtoMapper;
import com.example.serverchillrate.dto.RequestInfluxQueryOptions;
import com.example.serverchillrate.models.AuthUser;
import com.example.serverchillrate.services.InfluxDBService;
import com.example.serverchillrate.services.TeamClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/client")
public class ClientController {
    private final TeamClientService service;
    private final InfluxDBService influxDBService;
    @Operation(summary = "Добавление в распределение",description = "По ссылке администратора зарегистрированный пользователь добавляется в список ожидания распределения по командам")
    @GetMapping("/{adminId}")
    @ResponseStatus(HttpStatus.OK)
    public void addToWait(@Parameter(name = "adminURL",required = true) @PathVariable UUID adminId,
                          @Parameter(description = "данные получаемые из jwt токена") @AuthenticationPrincipal AuthUser authUser){
        service.addUserToWait(authUser.getUuid(),adminId);
    }
    @Operation(summary = "Отправка данных",description = "Отправка данныз с датчика на сервер")
    @PostMapping("/data")
    @ResponseStatus(HttpStatus.OK)
    public void sendData(@Parameter(name = "list",required = true) @RequestBody ListInfluxPointDto list,
                         @Parameter(description = "данные получаемые из jwt токена") @AuthenticationPrincipal AuthUser authUser){
        influxDBService.sendListData(list,authUser.getUuid());
    }
    @Operation(summary = "получить данные",description = "получить данные по определённому датчику, при отсутсвии настроек запроса в бд, берутся данные за последние 15 минут без агрегирования")
    @GetMapping("/data/{typeSensor}")
    public ResponseEntity<List<PointDto>> getData(@Parameter(required = true) @PathVariable String typeSensor,
                                                  @Parameter(description = "данные получаемые из jwt токена") @AuthenticationPrincipal AuthUser authUser,
                                                  @RequestBody(required = false) RequestInfluxQueryOptions options){
        if(options==null){
            options=new RequestInfluxQueryOptions();
        }
        return ResponseEntity.ok(PointDtoMapper.INSTANCE.toListDto( influxDBService.getData(typeSensor,authUser.getUuid(),options)));
    }

}
