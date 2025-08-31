package com.example.serverchillrate.services.impl;

import com.example.serverchillrate.dto.RequestInfluxQueryOptions;
import com.example.serverchillrate.entity.InfluxPointApp;
import com.example.serverchillrate.entity.Team;
import com.example.serverchillrate.models.UserTemp;
import com.example.serverchillrate.repository.TeamRepository;
import com.example.serverchillrate.repository.UserRepository;
import com.example.serverchillrate.services.InfluxDBService;
import com.example.serverchillrate.services.TeamClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TeamClientServiceImpl implements TeamClientService {
    private final TeamRepository repository;
    private final UserRepository userRepository;
    private final InfluxDBService influxDBService;
    private final HashMap<UUID,HashSet<UserTemp>> adminTempUser;
    @Override
    public Team addUser(Long teamId, UUID userID, UUID adminID) {
        var admin=userRepository.findById(adminID).orElseThrow();
        var client=userRepository.findById(userID).orElseThrow();
        var team= repository.findById(teamId).orElseThrow();
        if(team.getAdmin()!=admin){
            throw new UsernameNotFoundException("it not admin team");
        }
        if(team.getClients().stream().anyMatch(c->c.getId().equals(userID))){
            throw new UsernameNotFoundException("He already have in team");
        }
        if(adminTempUser.get(admin.getId()).stream().anyMatch(c->c.getUser().getId().equals(client.getId()))){
            adminTempUser.get(admin.getId()).removeIf(c->c.getUser().getId().equals(client.getId()));
            team.getClients().add(client);
            return repository.save(team);
        }
        else{
            throw new UsernameNotFoundException("not contains user");
        }
    }

    @Override
    public void addUserToWait(UUID clientId,UUID adminId) {
        var admin=userRepository.findByAdminToken(adminId).orElseThrow();
        if(!adminTempUser.containsKey(admin.getId())){
            adminTempUser.put(admin.getId(),new HashSet<>());
        }
        var client=userRepository.findById(clientId).orElseThrow();
        adminTempUser.get(admin.getId()).add(new UserTemp(client, new Date()));
    }

    @Override
    public Team deleteUser(Long teamId,UUID userId,UUID adminId) {
        var team= repository.findById(teamId).orElseThrow();
        var admin=userRepository.findById(adminId).orElseThrow();
        if(team.getAdmin()!=admin){
            throw new UsernameNotFoundException("it not admin team");
        }
        team.getClients().remove(userRepository.findById(userId).orElseThrow());
        repository.save(team);
        return team;
    }

    @Override
    public List<InfluxPointApp> getUserData(Long teamId, UUID clientID, UUID adminId, String typeSensor,
                                            RequestInfluxQueryOptions options) {
        var team=repository.findById(teamId).orElseThrow();
        if(!team.getAdmin().getId().equals(adminId)){
            throw new UsernameNotFoundException("This admin have not owner");
        }
        if(team.getClients().stream().noneMatch(c->c.getId().equals(clientID))){
            throw new UsernameNotFoundException("Not found client");
        }

        return influxDBService.getData(typeSensor,clientID,options);
    }
}
