package com.example.serverchillrate.models;

import com.example.serverchillrate.entity.UserApp;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/*
Пара userApp и времени
*/
@Data
@AllArgsConstructor
public class UserTemp {
    UserApp user;
    Date date;
}
