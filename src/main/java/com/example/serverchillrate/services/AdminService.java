package com.example.serverchillrate.services;

import com.example.serverchillrate.entity.UserApp;

import java.util.List;
import java.util.UUID;

public interface AdminService {
     String getUrl(UUID id);
     List<UserApp> getUsersWait(UUID idAdmin);
     String updateUrl(UUID id);
}
