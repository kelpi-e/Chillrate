package com.example.serverchillrate.dto;

import com.example.serverchillrate.entity.InfluxPointApp;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "струкутра отправки данных с датчиков")
@Data
@Builder
public class InfluxPointAndSensorType {
    @Pattern(regexp = "^[a-zA-z]\\d*$",message = "Неверное имя датчика")
    @Schema(description = "имя датчика",defaultValue = "temp1")
    String sensorType;
    @Schema(description = "лист данных")
    List<PointDto> listPoint;
}
