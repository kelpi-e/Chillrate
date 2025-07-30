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

@Service
@RequiredArgsConstructor
public class CRUDTeamImpl implements CRUDTeam {
    private final TeamRepository repository;
    private final UserRepository userRepository;
    @Override
    public Team create(TeamDto dto, String emailAdmin) {
        var admin=userRepository.findByEmail(emailAdmin).orElseThrow();
        return repository.save(Team.builder().name(dto.getName()).admin(admin).clients(new ArrayList<>()).build());
    }

    @Override
    public Team read(Long id,String emailAdmin) {
        var admin=userRepository.findByEmail(emailAdmin).orElseThrow();
        var team=repository.findById(id).orElseThrow();
        if(team.getAdmin()!=admin){
            throw new UsernameNotFoundException("it not admin team");
        }
        return team;
    }

    @Override
    public Team update(TeamDto dto, String emailAdmin) {
        var admin=userRepository.findByEmail(emailAdmin).orElseThrow();
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
    public List<Team> getListTeam(String emailAdmin) {
        var admin=userRepository.findByEmail(emailAdmin).orElseThrow();
        return repository.findAllByAdmin(admin);
    }

    @Override
    public void delete(Long id, String emailAdmin) {
        var admin=userRepository.findByEmail(emailAdmin).orElseThrow();
        var team=repository.findById(id).orElseThrow();

        if(team.getAdmin()!=admin){
            throw new UsernameNotFoundException("it not admin team");
        }
        repository.delete(team);
    }
}
