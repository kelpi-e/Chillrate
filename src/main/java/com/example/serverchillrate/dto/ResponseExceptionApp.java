package com.example.serverchillrate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
@Schema(description = "ответ от сервера в случае ошибки")
@Data
@Builder
public class ResponseExceptionApp {
    @Schema(example = "jwt invalid")
    String message;
}
