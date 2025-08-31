package com.example.serverchillrate.services.impl;

import com.example.serverchillrate.config.InfluxDBClientConfig;
import com.example.serverchillrate.dto.ListInfluxPointDto;
import com.example.serverchillrate.dto.PointDto;
import com.example.serverchillrate.dto.PointDtoMapper;
import com.example.serverchillrate.dto.RequestInfluxQueryOptions;
import com.example.serverchillrate.entity.InfluxPointApp;
import com.example.serverchillrate.services.InfluxDBService;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import kotlin.TypeCastException;
import lombok.RequiredArgsConstructor;
import org.hibernate.TypeMismatchException;
import org.springframework.stereotype.Service;
import com.influxdb.client.domain.WritePrecision;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InfluxDbServiceImpl implements InfluxDBService {
    private final InfluxDBClient influxDBClient;
    private final InfluxDBClientConfig influxDBClientConfig;
    private final HashMap<String,Class<?>> sensorToType;
    private final HashMap<UUID, List<InfluxPointApp>> uuidToListData;

    @Override
    public void sendListData(ListInfluxPointDto influxPointList,UUID user) {
        if(influxPointList!=null){
            for(var e:influxPointList.getList()){
                String sensorType=e.getSensorType();
                var contains=sensorToType.containsKey(sensorType);
                if (!contains){
                    sensorToType.put(e.getSensorType(),e.getListPoint().get(0).getValue().getClass());
                }
                var type=sensorToType.get(e.getSensorType());
                for(var el:e.getListPoint()){
                    if(el.getValue().getClass()!=type){
                        throw new ClassCastException();
                    }
                    el.setUuid(user.toString());
                    el.setSensorType(sensorType);
                    var influxPointApp=PointDtoMapper.INSTANCE.toEntity(el);
                    uuidToListData.computeIfAbsent(user, k -> new ArrayList<>());
                    uuidToListData.get(user).add(influxPointApp);
                    sendData(influxPointApp);
                }
            }
        }
    }


    private void sendData(InfluxPointApp influxPoint) {
        WriteApiBlocking writeApiBlocking=influxDBClient.getWriteApiBlocking();
        var newPoint=new Point(influxPoint.getSensorType())
                .addTag("user",influxPoint.getUuid())
                .addField("value",influxPoint.getValue())
                .time(influxPoint.getTime().toEpochMilli(),WritePrecision.MS);
        writeApiBlocking.writePoint(newPoint);
    }

    @Override
    public List<InfluxPointApp> getData(String typeSensor, UUID userID, RequestInfluxQueryOptions options) {
        if(options.getStart().equals("-15m")&&options.getAggregate()==null){
            return  uuidToListData.get(userID);
        }
        String query="from(bucket: \""+influxDBClientConfig.getBucket()+"\")\n" +
                "  |> range(start:"+options.getStart()+" )\n" +
                "  |> filter(fn: (r) => r[\"_measurement\"] == \""+typeSensor+"\"  and r[\"user\"]==\""+userID+"\")\n";
        if(options.getAggregate()!=null){
            query=query+"|> aggregateWindow(every:"+options.getAggregate()+", fn: mean, createEmpty: false)";
        }
        return influxDBClient.getQueryApi().query(query,InfluxPointApp.class);
    }
}
