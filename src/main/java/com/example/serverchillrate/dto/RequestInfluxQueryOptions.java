package com.example.serverchillrate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Schema(description = "опции для получения данных из бд")
public class RequestInfluxQueryOptions {
    @Builder.Default
    @Pattern(regexp = "^-\\d*(w|d|h|m|s)$")
    @Schema(description = "время в отрицательном формате, показывающий за какой период брать точки(пример за последние 15 мин=-15m). Взять все точки 0",example = "-15s")
    String start="-15m";
    @Builder.Default
    @Pattern(regexp = "^\\d*(w|d|h|m|s)$",message = "Invalid set aggregate value")
    @Schema(description = "понижение дискритезации точек за счёт усреднённого значения за определённый период",example = "10m")
    String aggregate=null;
}
