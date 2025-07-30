package com.example.serverchillrate.services;

import com.example.serverchillrate.dto.TeamDto;
import com.example.serverchillrate.entity.Team;
import com.example.serverchillrate.entity.UserApp;

import java.util.List;

public interface CRUDTeam {
    Team create(TeamDto dto, String emailAdmin);
    Team read(Long id,String emailAdmin);
    Team update(TeamDto dto,String emailAdmin);
    List<Team> getListTeam(UserApp adminId);
    List<Team> getListTeam(String emailAdmin);
    void delete(Long id,String emailAdmin);
}
