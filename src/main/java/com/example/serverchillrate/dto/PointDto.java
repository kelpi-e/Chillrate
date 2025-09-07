package com.example.serverchillrate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.influxdb.annotations.Column;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Data
@Builder
@Schema(description = "сущность точки в бд")
public class PointDto {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(defaultValue = "2025-08-22 20:33:00")
    private LocalDateTime time;
    @Schema(description = "числовое значение датчика",defaultValue = "12.3")
    private Number value;
    @Schema(defaultValue = "id пользователя, отправившего данные")
    private String uuid;
    @Pattern(regexp = "^[a-zA-z]\\d*$",message = "Неверное имя датчика")
    @Schema(defaultValue = "имя датчика",description = "temp1")
    private String sensorType;
    public Instant timeToInstant(){
        return time.toInstant(ZoneOffset.UTC);
    }
}
