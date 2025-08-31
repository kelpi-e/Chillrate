package com.example.serverchillrate.scheduling;

import com.example.serverchillrate.models.UserTemp;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ClearUserTempServiceImpl  {
    private final HashMap<UUID, UserTemp> tempUsers;
    private final HashMap<UUID, HashSet<UserTemp>> adminToTempUsers;
    @Scheduled(cron = "@hourly")
    public void clearNotValidUser() {
        Date dateNow=new Date();
        tempUsers.entrySet().removeIf(el->{
            long diffTime=dateNow.getTime()-el.getValue().getDate().getTime();
            long diffHour=TimeUnit.MILLISECONDS.toHours(diffTime);
            return diffHour>=1;
        });
    }
    @Scheduled(cron = "@hourly")
    public void clearWaitUser(){
        Date dateNow=new Date();
       adminToTempUsers.forEach((key, value) -> value.removeIf(el ->
               TimeUnit.MILLISECONDS.toHours(dateNow.getTime() - el.getDate().getTime()) >= 1));
    }
}
