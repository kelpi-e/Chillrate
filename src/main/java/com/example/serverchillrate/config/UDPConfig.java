package com.example.serverchillrate.config;

import com.example.serverchillrate.dto.UserDataMapper;
import com.example.serverchillrate.dto.UserMapper;
import com.example.serverchillrate.models.PairUserAndData;
import com.example.serverchillrate.entity.UserData;

import com.example.serverchillrate.repository.UserDataRepository;
import com.example.serverchillrate.secutiry.Role;
import com.example.serverchillrate.secutiry.service.UdpServiceSecure;
import com.example.serverchillrate.services.CRUDTeam;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.ip.dsl.Udp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/// @ struct configuration Udp server (Test working as echo)
@Configuration
@RequiredArgsConstructor
public class UDPConfig {
    private final HashMap<UUID,PairUserAndData> uuidToData;
    private final HashMap<String,UUID> jwtTOUuid;
    private final UdpServiceSecure serviceSecure;
    private final UserDataRepository repository;
    private final CRUDTeam crudTeam;

    Logger logger= LoggerFactory.getLogger(UDPConfig.class);
    /// @brief конфигурация endpoint клиента для полученния данных с датчика
    /// @details порт udp 8080 принимает json с параметрами jwt(String),
    /// dateTime(pattern: yyyy-MM-dd HH:mm:ss), data(String-json)
    /// @return endpoint return "OK"-удалось сохранить данные
    /// "jwt Invalid"-ошибка из-за jwt  "Parse exception"-ошибка в передаче данных
    @Bean
    public IntegrationFlow ClientUdpConfig() {

        return IntegrationFlow.from(Udp.inboundAdapter(8080).id("udpIn"))
                .<byte[], String>transform((p) -> {
                    String request=new String(p);
                    logger.info(request);
                    String dataField="data";
                    try{
                    JSONObject jsonObject=(JSONObject) JSONValue.parse(request);
                    logger.info(jsonObject.toJSONString());
                    String jwt=(String)jsonObject.get("jwt");
                    if(!serviceSecure.checkToken(jwt))
                        return "jwt Invalid";
                    String dateStr=(String) jsonObject.get("dateTime");
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date datetime=simpleDateFormat.parse(dateStr);
                    var dataDetails=uuidToData.get( jwtTOUuid.get(jwt));
                    UserData userData=UserData.builder().
                            _user(dataDetails.getUser())
                            .dateTime(datetime)
                            .data(request.substring(request.indexOf(dataField)+dataField.length()+3,request.length()-1))
                            .build();
                    repository.save(userData);
                    dataDetails.userData.add(userData);
                    }catch (Exception exception){
                        logger.info(exception.getMessage());
                        return "parse exception";
                    }
                   return "OK";
                })
                .handle(Udp.outboundAdapter("headers['ip_packetAddress']")
                        .socketExpression("@udpIn.socket"))
                .get();
    }
    ///@brief настрока порта для отправки данных тренеру за последнее время
    ///@param отправка сообщений в виде json с параметрами jwt(String)
    ///@return в виде json массив из данных по каждому пользователю
    ///@details в случае неверного токена вернёт строку: "jwt invalid"
    //// неверный формат:"parse exception"
    @Bean
    public IntegrationFlow AdminUdpConfig(){
        return IntegrationFlow.from(Udp.inboundAdapter(8081).id("UdpAdminIn"))
                .<byte[],String>transform(p->{
                    logger.info(new String(p));
                    try {
                        JSONObject jsonObject = (JSONObject) JSONValue.parse(new String(p));
                        logger.info(jsonObject.toJSONString());
                        String jwt = (String) jsonObject.get("jwt");
                        var admin=serviceSecure.getAdminFromToken(jwt);
                        if(admin.getRole()!= Role.ADMIN){
                            return "He is not admin";
                        }
                        var listTeam=crudTeam.getListTeam(admin);
                        StringBuilder stringBuilder=new StringBuilder();
                        stringBuilder.append('{');
                        ObjectMapper objectMapper=new ObjectMapper();

                        boolean[] firsts={true,true,true};

                        for (var team : listTeam) {
                            if(!firsts[0]){
                                stringBuilder.append(',');
                            }
                            firsts[0]=false;
                            stringBuilder.append('"');
                            stringBuilder.append(team.getId().toString());
                            stringBuilder.append('"');
                            stringBuilder.append(":{");
                            firsts[1]=true;


                            for (var client : team.getClients()) {
                                try {
                                    StringBuilder userStr = new StringBuilder();
                                    if (!firsts[1]) {
                                        userStr.append(',');
                                    }
                                    firsts[1] = false;
                                    userStr.append('"');
                                    userStr.append(client.getEmail());
                                    userStr.append('"');
                                    userStr.append(":");
                                    userStr.append("[");
                                    var ListData = uuidToData.get(client.getId()).userData;
                                    firsts[2] = true;
                                    for (var data : ListData) {
                                        if (!firsts[2]) {
                                            userStr.append(",");
                                        }
                                        firsts[2] = false;
                                        userStr.append(objectMapper.writeValueAsString(UserDataMapper.INSTANCE.toDto(data)));
                                    }
                                    userStr.append("]");
                                    stringBuilder.append(userStr);
                                }
                                catch (Exception ignored){

                                }
                            }


                            stringBuilder.append("}");
                        }
                        stringBuilder.append("}");
                        return stringBuilder.toString();
                    }
                    catch (Exception exception){
                        return "parse exception";
                    }
                }) .handle(Udp.outboundAdapter("headers['ip_packetAddress']")
                        .socketExpression("@UdpAdminIn.socket")).get();
    }

}