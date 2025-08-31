package com.example.serverchillrate.services.impl;

import com.example.serverchillrate.entity.UserApp;
import com.example.serverchillrate.models.ServerData;
import com.example.serverchillrate.models.UserTemp;
import com.example.serverchillrate.repository.TeamRepository;
import com.example.serverchillrate.repository.UserRepository;
import com.example.serverchillrate.services.AdminService;
import com.example.serverchillrate.services.InfluxDBService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final ServerData serverData;
    private final UserRepository userRepository;
    private final InfluxDBService influxDBService;
    private final HashMap<UUID, HashSet<UserTemp>> uuidAdminToWaitUsers;
    private final TeamRepository teamRepository;
    @Override
    public String getUrl(UUID adminId) {
        var user=userRepository.findById(adminId).orElseThrow();
        return serverData.getExternalHost()+":"+serverData.getExternalPort()+"/api/v1/client/"+user.getAdminToken();
    }

    @Override
    public List<UserApp> getUsersWait(UUID adminId) {
        var user=userRepository.findById(adminId).orElseThrow();
        var tempUsers=uuidAdminToWaitUsers.get(user.getId());
        if(tempUsers==null){
            return new ArrayList<>();
        }
        return tempUsers.stream().map(UserTemp::getUser).collect(Collectors.toList());
    }

}
