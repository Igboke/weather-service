package com.example.weather_service.dto;

public record CurrentWeatherResponse(    
    String city,
    double temperature,
    String country,
    String timestamp,
    String sunrise,
    String sunset,
    int humidity,
    int pressure,
    float windSpeed,
    float windDirection,
    String conditions,
    String description,
    String iconClass) {
}
