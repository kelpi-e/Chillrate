package com.example.serverchillrate.models;

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
