package com.example.weather_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApiErrorResponse {
    private final int statusCode;
    private final String message;
    private final LocalDateTime timestamp = LocalDateTime.now();
}
