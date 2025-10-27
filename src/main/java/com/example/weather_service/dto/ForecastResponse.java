package com.example.weather_service.dto;

import java.util.List;

public record ForecastResponse(
    String city,
    String country,
    List<DailyForecast> forecasts
) {
    
}


