package com.example.serverchillrate.services.impl;

import com.example.serverchillrate.entity.UserApp;
import com.example.serverchillrate.models.ServerData;
import com.example.serverchillrate.models.UserTemp;
import com.example.serverchillrate.repository.UserRepository;
import com.example.serverchillrate.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final ServerData serverData;
    private final UserRepository userRepository;
    private final HashMap<UUID, HashSet<UserTemp>> uuidAdminToWaitUsers;
    @Override
    public String getUrl(String emailAdmin) {
        var user=userRepository.findByEmail(emailAdmin).orElseThrow();
        return serverData.getExternalHost()+":"+serverData.getExternalPort()+"/api/v1/client/"+user.getId();
    }

    @Override
    public List<UserApp> getUsersWait(String emailAdmin) {
        var user=userRepository.findByEmail(emailAdmin).orElseThrow();
        var tempUsers=uuidAdminToWaitUsers.get(user.getId());
        return tempUsers.stream().map(UserTemp::getUser).collect(Collectors.toList());
    }
}
