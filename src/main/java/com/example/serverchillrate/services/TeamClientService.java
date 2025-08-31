package com.example.serverchillrate.services;

import com.example.serverchillrate.dto.RequestInfluxQueryOptions;
import com.example.serverchillrate.entity.InfluxPointApp;
import com.example.serverchillrate.entity.Team;

import java.util.List;
import java.util.UUID;

public interface TeamClientService {
    Team addUser(Long teamId, UUID userID, UUID adminId);
    void addUserToWait(UUID clientId,UUID adminId);
    Team deleteUser(Long teamId, UUID userID,UUID adminId);
    List<InfluxPointApp> getUserData(Long teamId, UUID clientID, UUID adminId, String typeSensor, RequestInfluxQueryOptions options);
}
