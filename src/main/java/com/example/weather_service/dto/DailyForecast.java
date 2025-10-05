package com.example.weather_service.dto;

import lombok.Data;

@Data
public class DailyForecast {
    private final String date;
    private final double maxTemperature;
    private final double minTemperature;
    private final String description;
}
