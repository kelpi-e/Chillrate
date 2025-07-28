package com.example.serverchillrate.services.impl;

import com.example.serverchillrate.dto.TeamDto;
import com.example.serverchillrate.models.Team;
import com.example.serverchillrate.models.UserApp;
import com.example.serverchillrate.repository.TeamRepository;
import com.example.serverchillrate.repository.UserRepository;
import com.example.serverchillrate.services.CRUDTeam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CRUDTeamImpl implements CRUDTeam {
    private final TeamRepository repository;
    private final UserRepository userRepository;
    @Override
    public Team create(TeamDto dto, String emailAdmin) {
        var admin=userRepository.findByEmail(emailAdmin).orElseThrow();
        return repository.save(Team.builder().name(dto.getName()).admin(admin).build());
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
    public void delete(Long id, String emailAdmin) {
        var admin=userRepository.findByEmail(emailAdmin).orElseThrow();
        var team=repository.findById(id).orElseThrow();
        if(team.getAdmin()!=admin){
            throw new UsernameNotFoundException("it not admin team");
        }
        repository.delete(team);
    }
}
