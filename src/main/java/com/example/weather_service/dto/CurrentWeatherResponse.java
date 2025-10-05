package com.example.weather_service.dto;

import lombok.Data;

// This is Lombok. It generates getters, setters, equals, hashCode, and a constructor. very important writing it manually is prone to errors
@Data 
public class CurrentWeatherResponse {
    private final String city;
    private final double temperature;
    private final String country;
    private final String timestamp;
    private final String sunrise;
    private final String sunset;
    private final int humidity;
    private final int pressure;
    private final float windSpeed;
    private final float windDirection;
    private final String conditions;
    private final String description;
}
