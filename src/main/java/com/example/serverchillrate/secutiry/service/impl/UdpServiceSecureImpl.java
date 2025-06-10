package com.example.serverchillrate.secutiry.service.impl;

import com.example.serverchillrate.models.PairUserAndData;
import com.example.serverchillrate.models.UserApp;
import com.example.serverchillrate.secutiry.jwt.JwtService;
import com.example.serverchillrate.secutiry.service.UdpServiceSecure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UdpServiceSecureImpl implements UdpServiceSecure {
    private final HashMap<UUID, PairUserAndData> uuidToData;
    private final HashMap<String,UUID> jwtTOUuid;
    private final JwtService jwtService;
    @Override
    public void addClientData(UserApp userApp, String jwt) {
        if(!uuidToData.containsKey(userApp.getId())){
            uuidToData.put(userApp.getId(),PairUserAndData.builder().build());
        }
        if(!jwtTOUuid.containsKey(jwt)){
            jwtTOUuid.put(jwt,userApp.getId());
        }

    }

    @Override
    public boolean checkToken(String jwt) {
       if(!jwtTOUuid.containsKey(jwt)) {
           return false;
       }
       var uuid=jwtTOUuid.get(jwt);
       if(!uuidToData.containsKey(uuid)){
           jwtTOUuid.remove(jwt);
           return false;
       }
       var userDetails= uuidToData.get(uuid).getUser();
       if(!jwtService.isTokenValid(jwt,userDetails)){
           jwtTOUuid.remove(jwt);
           return false;
       }
       return true;
    }
}
