package com.example.serverchillrate.services;

import com.example.serverchillrate.entity.UserApp;

import java.util.List;

public interface AdminService {
     String getUrl(String emailAdmin);
     List<UserApp> getUsersWait(String emailAdmin);
}
