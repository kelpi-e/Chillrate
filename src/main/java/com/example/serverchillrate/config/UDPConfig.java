package com.example.serverchillrate.config;

import com.example.serverchillrate.models.PairUserAndData;
import com.example.serverchillrate.models.UserApp;
import com.example.serverchillrate.models.UserData;

import com.example.serverchillrate.repository.UserDataRepository;
import com.example.serverchillrate.secutiry.service.UdpServiceSecure;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.catalina.User;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.ip.dsl.Udp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/// @ struct configuration Udp server (Test working as echo)
@Configuration
@RequiredArgsConstructor
public class UDPConfig {
    private  final HashMap<UUID,PairUserAndData> uuidToData;
    private  final HashMap<String,UUID> jwtTOUuid;
    private final UdpServiceSecure serviceSecure;
    private final UserDataRepository repository;
    Logger logger= LoggerFactory.getLogger(UDPConfig.class);
    /// Test echo port 11111
    @Bean
    public IntegrationFlow udpEchoUpcaseServer() {

        return IntegrationFlow.from(Udp.inboundAdapter(8080).id("udpIn"))
                .<byte[], String>transform((p) -> {
                    JSONObject jsonObject=(JSONObject) JSONValue.parse(new String(p));
                    String jwt=(String)jsonObject.get("jwt");
                    if(!serviceSecure.checkToken(jwt)){
                        return  "jwt Invalid";
                    }
                    try{
                        String dateStr=(String) jsonObject.get("datetime");
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date datetime=simpleDateFormat.parse(dateStr);
                        var dataDetails=uuidToData.get( jwtTOUuid.get(jwt));
                        UserData userData=UserData.builder().
                                _user(dataDetails.getUser())
                                .dateTime(datetime)
                                .build();
                        repository.save(userData);
                        dataDetails.userData.add(userData);
                    }catch (Exception exception){
                        return "Parse exception";
                    }
                   return "OK";
                })
                .handle(Udp.outboundAdapter("headers['ip_packetAddress']")
                        .socketExpression("@udpIn.socket"))
                .get();
    }


}