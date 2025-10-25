package com.example.weather_service.dto;

public record DailyForecast(
    String date,
    double maxTemperature,
    double minTemperature,
    String description,
    String conditions,
    double humidity,
    int pressure,
    double windSpeed,
    float windDirection,
    float rainProbability,
    double rainMillimeter,
    String iconClass

) {

}
