package com.example.serverchillrate.models;

import lombok.Builder;
import lombok.Data;
import org.junit.experimental.theories.DataPoints;

import java.util.List;

/// @struct
@Data
@Builder
public class PairUserAndData {
    UserApp user;
    public List<UserData> userData;
}
