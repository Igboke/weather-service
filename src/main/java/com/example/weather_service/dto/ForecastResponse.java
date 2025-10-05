package com.example.weather_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class ForecastResponse {
    private final String city;
    private final String country;
    private final List<DailyForecast> forecasts;
    
}


