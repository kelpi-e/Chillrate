package com.example.serverchillrate.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListInfluxPointDto {
    List<InfluxPointAndSensorType> list;
}
