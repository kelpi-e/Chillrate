package com.example.serverchillrate.dto;

import com.example.serverchillrate.entity.InfluxPointApp;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class InfluxPointAndSensorType {
    String sensorType;
    List<PointDto> listPoint;
}
