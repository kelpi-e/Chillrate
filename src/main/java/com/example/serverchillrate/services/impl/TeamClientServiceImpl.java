package com.example.serverchillrate.services.impl;

import com.example.serverchillrate.models.Team;
import com.example.serverchillrate.models.UserApp;
import com.example.serverchillrate.models.UserTemp;
import com.example.serverchillrate.repository.TeamRepository;
import com.example.serverchillrate.repository.UserRepository;
import com.example.serverchillrate.services.TeamClientService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamClientServiceImpl implements TeamClientService {
    private final TeamRepository repository;
    private final UserRepository userRepository;
    private final HashMap<UUID,HashSet<UserTemp>> adminTempUser;
    @Override
    public Team addUser(Long teamId, UUID clientId, String emailAdmin) {
        var admin=userRepository.findByEmail(emailAdmin).orElseThrow();
        var client=userRepository.findById(clientId).orElseThrow();
        var team= repository.findById(teamId).orElseThrow();
        if(team.getAdmin()!=admin){
            throw new UsernameNotFoundException("it not admin team");
        }
        if(adminTempUser.get(admin.getId()).stream().anyMatch(c->c.getUser().equals(client))){
            adminTempUser.get(admin.getId()).removeIf(c->c.getUser().equals(client));
            team.getClients().add(client);
            return repository.save(team);
        }
        else{
            throw new UsernameNotFoundException("not contains user");
        }
    }

    @Override
    public void addUserToWait(String emailClient,UUID adminId) {
        if(!adminTempUser.containsKey(adminId)){
            throw new UsernameNotFoundException("not found admin");
        }
        var client=userRepository.findByEmail(emailClient).orElseThrow();
        adminTempUser.get(adminId).add(new UserTemp(client, new Date()));
    }

    @Override
    public Team deleteUser(Long teamId, UUID clientId,String emailAdmin) {
        var team= repository.findById(teamId).orElseThrow();
        var admin=userRepository.findByEmail(emailAdmin).orElseThrow();
        if(team.getAdmin()!=admin){
            throw new UsernameNotFoundException("it not admin team");
        }
        team.getClients().remove(userRepository.findById(clientId).orElseThrow());
        repository.save(team);
        return team;
    }
}
