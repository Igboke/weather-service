package com.example.weather_service.service;

import com.example.weather_service.dto.CurrentWeatherResponse;
import com.example.weather_service.dto.ForecastResponse;

public interface WeatherService {

    CurrentWeatherResponse getWeather(String city);
    ForecastResponse getForecast(String city);
} 
