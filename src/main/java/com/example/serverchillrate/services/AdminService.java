package com.example.serverchillrate.services;

import com.example.serverchillrate.entity.UserApp;
import com.example.serverchillrate.entity.UserData;

import java.util.List;

public interface AdminService {
     String getUrl(String emailAdmin);
     List<UserApp> getUsersWait(String emailAdmin);
     List<UserData> getUserDataByEmail(Long TeamID,String emailClient,String emailAdmin);
}
