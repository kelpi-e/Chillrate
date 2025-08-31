package com.example.serverchillrate.services;

import com.example.serverchillrate.dto.TeamDto;
import com.example.serverchillrate.entity.Team;
import com.example.serverchillrate.entity.UserApp;

import java.util.List;
import java.util.UUID;

public interface CRUDTeam {
    Team create(TeamDto dto, UUID adminId);
    Team read(Long id,UUID adminId);
    Team update(TeamDto dto,UUID adminId);
    List<Team> getListTeam(UserApp adminId);
    List<Team> getListTeam(UUID adminId);
    void delete(Long id,UUID adminId);
}
