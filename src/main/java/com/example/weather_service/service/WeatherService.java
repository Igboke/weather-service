package com.example.weather_service.service;

import com.example.weather_service.dto.CurrentWeatherResponse;

public interface WeatherService {

    CurrentWeatherResponse getWeather(String city);
} 
