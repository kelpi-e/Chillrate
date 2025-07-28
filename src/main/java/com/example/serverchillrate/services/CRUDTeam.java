package com.example.serverchillrate.services;

import com.example.serverchillrate.dto.TeamDto;
import com.example.serverchillrate.models.Team;
import com.example.serverchillrate.models.UserApp;

import java.util.List;
import java.util.UUID;

public interface CRUDTeam {
    Team create(TeamDto dto, String emailAdmin);
    Team read(Long id,String emailAdmin);
    Team update(TeamDto dto,String emailAdmin);
    List<Team> getListTeam(UserApp adminId);
    void delete(Long id,String emailAdmin);
}
