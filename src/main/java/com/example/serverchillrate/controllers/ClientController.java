package com.example.serverchillrate.controllers;

import com.example.serverchillrate.dto.ListInfluxPointDto;
import com.example.serverchillrate.dto.PointDto;
import com.example.serverchillrate.dto.PointDtoMapper;
import com.example.serverchillrate.dto.RequestInfluxQueryOptions;
import com.example.serverchillrate.models.AuthUser;
import com.example.serverchillrate.services.InfluxDBService;
import com.example.serverchillrate.services.TeamClientService;
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
    @GetMapping("/{adminId}")
    @ResponseStatus(HttpStatus.OK)
    public void addToWait(@PathVariable UUID adminId,
                          @AuthenticationPrincipal AuthUser authUser){
        service.addUserToWait(authUser.getUuid(),adminId);
    }
    @PostMapping("/data")
    @ResponseStatus(HttpStatus.OK)
    public void sendData(@RequestBody ListInfluxPointDto list,
                         @AuthenticationPrincipal AuthUser authUser){
        influxDBService.sendListData(list,authUser.getUuid());
    }
    @GetMapping("/data/{typeSensor}")
    public ResponseEntity<List<PointDto>> getData(@PathVariable String typeSensor,
                                                  @AuthenticationPrincipal AuthUser authUser,
                                                  @RequestBody(required = false) RequestInfluxQueryOptions options){
        if(options==null){
            options=new RequestInfluxQueryOptions();
        }
        return ResponseEntity.ok(PointDtoMapper.INSTANCE.toListDto( influxDBService.getData(typeSensor,authUser.getUuid(),options)));
    }

}
