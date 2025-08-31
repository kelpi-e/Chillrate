package com.example.serverchillrate.config;


import com.example.serverchillrate.entity.InfluxPointApp;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Getter
public class InfluxDBClientConfig {
    @Value("${spring.influxdb.url}")
    String url;
    @Value("${spring.influxdb.bucket}")
    String bucket;
    @Value("${spring.influxdb.org}")
    String org;
    @Value("${spring.influxdb.token}")
    String token;
    @Bean
    public InfluxDBClient influxDBClient(){
        return InfluxDBClientFactory.create(url,token.toCharArray(),org,bucket);
    }
    @Bean
    public HashMap<String,Class<?>> sensorToType(){
        String fluxQuery = "import \"influxdata/influxdb/schema\"\n" +
                "schema.measurements(bucket: \"" + bucket + "\")";

        List<String> measurements = new ArrayList<>();
        List<FluxTable> tables = influxDBClient().getQueryApi().query(fluxQuery, org);

        for (FluxTable fluxTable : tables) {
            for (FluxRecord fluxRecord : fluxTable.getRecords()) {
                // The measurement name is typically found in the "_value" column
                measurements.add(String.valueOf(fluxRecord.getValueByKey("_value")));
            }
        }
        HashMap<String,Class<?>> res=new HashMap<>();
        for(var e :measurements){
            var query=influxDBClient().getQueryApi().query("from(bucket:\"chillrate_sensors\")\n" +
                    "|>range(start: -1y)\n" +
                    "|>filter(fn: (r) => r._measurement == \""+e+"\")\n" +
                    "|>limit(n:1)");
            for (FluxTable table : query) {
                for (FluxRecord record : table.getRecords()) {
                    Map<String, Object> values = record.getValues();
                    res.put(e,values.get("_value").getClass());
                }
            }
        }
        return  res;
    }
}
