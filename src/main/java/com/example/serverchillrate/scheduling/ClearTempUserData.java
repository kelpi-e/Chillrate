package com.example.serverchillrate.scheduling;

import com.example.serverchillrate.entity.InfluxPointApp;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
public class ClearTempUserData {
    private final HashMap<UUID, List<InfluxPointApp>> uuidTOUserAndData;
    @Scheduled(cron = "0 0/15 * * * ?")
    public void clearUserDataTemp(){
        uuidTOUserAndData.forEach((key, value) -> value.removeIf(el -> el.getTime().isBefore(Instant.now().plus(15, ChronoUnit.MINUTES))));
    }
}
