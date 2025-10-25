package com.example.weather_service.dto;

import java.time.LocalDateTime;

public record ApiErrorResponse(
    int statusCode,
    String message,
    LocalDateTime timestamp
) {
    public ApiErrorResponse{
        if (timestamp == null){
            timestamp = LocalDateTime.now();
        }
    }
} 
