package com.example.serverchillrate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.influxdb.annotations.Column;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Data
@Builder
public class PointDto {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    private Number value;
    private String uuid;
    @Pattern(regexp = "^[a-z]{1,10}\\d*$",message = "Неверное имя датчика")
    private String sensorType;
    public Instant timeToInstant(){
        return time.toInstant(ZoneOffset.UTC);
    }
}
