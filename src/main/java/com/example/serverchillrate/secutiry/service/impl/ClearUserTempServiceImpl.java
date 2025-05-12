package com.example.serverchillrate.secutiry.service.impl;

import com.example.serverchillrate.models.UserTemp;
import com.example.serverchillrate.secutiry.service.ClearUserTempService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ClearUserTempServiceImpl implements ClearUserTempService {
    private final HashMap<UUID, UserTemp> tempUsers;
    @Override
    @Scheduled(cron = "@hourly")
    public void clearNotValidUser() {
        Date dateNow=new Date();
        tempUsers.entrySet().removeIf(el->{
            long diffTime=el.getValue().getDate().getTime()-dateNow.getTime();
            long diffHour=TimeUnit.MILLISECONDS.toHours(diffTime);
            return diffHour>=1;
        });
    }
}
