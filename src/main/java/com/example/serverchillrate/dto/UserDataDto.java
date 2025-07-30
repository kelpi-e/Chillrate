package com.example.serverchillrate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
@Data
@Builder
public class UserDataDto {
    String data;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date dateTime;
}
