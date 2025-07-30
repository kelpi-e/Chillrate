package com.example.serverchillrate.models;

import com.example.serverchillrate.entity.UserApp;
import com.example.serverchillrate.entity.UserData;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/// @struct
@Data
@Builder
public class PairUserAndData {
    UserApp user;
    public List<UserData> userData;
}
