package com.example.weather_service.dto;

import lombok.Data;

// This is Lombok. It generates getters, setters, equals, hashCode, and a constructor. very important writing it manually is prone to errors
@Data 
public class CurrentWeatherResponse {
    private final String city;
    private final double temperature;
    
}
