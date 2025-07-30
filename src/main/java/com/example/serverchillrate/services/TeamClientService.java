package com.example.serverchillrate.services;

import com.example.serverchillrate.entity.Team;

import java.util.UUID;

public interface TeamClientService {
    Team addUser(Long teamId, String emailUser, String emailAdmin);
    void addUserToWait(String emailClient,UUID adminId);
    Team deleteUser(Long teamId, String emailUser,String emailAdmin);

}
