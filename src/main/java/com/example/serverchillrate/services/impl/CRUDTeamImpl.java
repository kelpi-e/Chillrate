package com.example.serverchillrate.services.impl;

import com.example.serverchillrate.dto.TeamDto;
import com.example.serverchillrate.entity.Team;
import com.example.serverchillrate.entity.UserApp;
import com.example.serverchillrate.repository.TeamRepository;
import com.example.serverchillrate.repository.UserRepository;
import com.example.serverchillrate.services.CRUDTeam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CRUDTeamImpl implements CRUDTeam {
    private final TeamRepository repository;
    private final UserRepository userRepository;
    @Override
    public Team create(TeamDto dto, UUID adminId) {
        var admin=userRepository.findById(adminId).orElseThrow();
        return repository.save(Team.builder().name(dto.getName()).admin(admin).clients(new ArrayList<>()).build());
    }

    @Override
    public Team read(Long id,UUID adminId) {
        var admin=userRepository.findById(adminId).orElseThrow();
        var team=repository.findById(id).orElseThrow();
        if(team.getAdmin()!=admin){
            throw new UsernameNotFoundException("it not admin team");
        }
        return team;
    }

    @Override
    public Team update(TeamDto dto, UUID adminId) {
        var admin=userRepository.findById(adminId).orElseThrow();
        var team=repository.findById(dto.getId()).orElseThrow();
        if(team.getAdmin()!=admin){
            throw new UsernameNotFoundException("it not admin team");
        }
        team.setName(dto.getName());
        return repository.save(team);
    }

    @Override
    public List<Team> getListTeam(UserApp adminId) {
        return repository.findAllByAdmin(adminId);
    }

    @Override
    public List<Team> getListTeam(UUID adminId) {
        var admin=userRepository.findById(adminId).orElseThrow();
        return repository.findAllByAdmin(admin);
    }

    @Override
    public void delete(Long id,UUID adminId) {
        var admin=userRepository.findById(adminId).orElseThrow();
        var team=repository.findById(id).orElseThrow();

        if(team.getAdmin()!=admin){
            throw new UsernameNotFoundException("it not admin team");
        }
        repository.delete(team);
    }
}
