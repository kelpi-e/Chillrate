package com.example.serverchillrate.services.impl;

import com.example.serverchillrate.entity.UserApp;
import com.example.serverchillrate.entity.UserData;
import com.example.serverchillrate.models.ServerData;
import com.example.serverchillrate.models.UserTemp;
import com.example.serverchillrate.repository.TeamRepository;
import com.example.serverchillrate.repository.UserDataRepository;
import com.example.serverchillrate.repository.UserRepository;
import com.example.serverchillrate.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final UserDataRepository userDataRepository;
    private final HashMap<UUID, HashSet<UserTemp>> uuidAdminToWaitUsers;
    private final TeamRepository teamRepository;
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

    @Override
    public List<UserData> getUserDataByEmail(Long TeamID,String emailClient,String emailAdmin) {
        var team=teamRepository.findById(TeamID).orElseThrow();;
        if(!team.getAdmin().getEmail().equals(emailAdmin)){
            throw new UsernameNotFoundException("Not admin");
        }
        var user=userRepository.findByEmail(emailClient).orElseThrow();
        return userDataRepository.findUserDataBy_user(user);
    }
}
