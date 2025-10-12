package com.example.weather_service.dto;

import lombok.Data;

@Data
public class DailyForecast {
    private final String date;
    private final double maxTemperature;
    private final double minTemperature;
    private final String description;
    private final String conditions;
    private final double humidity;
    private final int pressure;
    private final double windSpeed;
    private final float windDirection;
    private final float rainProbability;
    private final double rainMillimeter;
    private final String iconClass;
}
