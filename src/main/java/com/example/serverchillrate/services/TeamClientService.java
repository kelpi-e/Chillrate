package com.example.serverchillrate.services;

import com.example.serverchillrate.models.Team;
import com.example.serverchillrate.models.UserApp;

import java.util.UUID;

public interface TeamClientService {
    Team addUser(Long teamId, UUID clientId, String emailAdmin);
    void addUserToWait(String emailClient,UUID adminId);
    Team deleteUser(Long teamId, UUID clientId,String emailAdmin);

}
