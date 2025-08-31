package com.example.serverchillrate.services;

import com.example.serverchillrate.dto.ListInfluxPointDto;
import com.example.serverchillrate.dto.RequestInfluxQueryOptions;
import com.example.serverchillrate.entity.InfluxPointApp;
import org.eclipse.angus.mail.util.UUDecoderStream;

import java.util.List;
import java.util.UUID;

public interface InfluxDBService {
     void sendListData(ListInfluxPointDto influxPointList,UUID user);
     List<InfluxPointApp> getData(String typeSensor, UUID userID, RequestInfluxQueryOptions options);
}
