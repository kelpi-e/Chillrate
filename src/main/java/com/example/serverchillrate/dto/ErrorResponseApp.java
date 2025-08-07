package com.example.serverchillrate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseApp {
    String message;
}
