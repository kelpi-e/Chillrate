package com.example.serverchillrate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RequestInfluxQueryOptions {
    @Pattern(regexp = "^-\\d*(w|d|h|m|s)$")
    String start="-15m";
    @Pattern(regexp = "^\\d*(w|d|h|m|s)$",message = "Invalid set aggregate value set")
    String aggregate=null;
}
