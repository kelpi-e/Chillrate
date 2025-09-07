package com.example.serverchillrate.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfluxPointApp {

    @Column(name = "time")
    private Instant time;
    @Column(name = "value")
    private Number value;
    @Column(name = "user")
    private String uuid;
    @Column(measurement = true)
    @Pattern(regexp = "^[a-zA-z]\\d*$",message = "Неверное имя датчика")
    private String sensorType;
    public LocalDateTime toLocalDateTime(){
        return LocalDateTime.ofInstant(time, ZoneId.systemDefault());
    }
}
