package com.example.weather_service.service;

import java.util.List;

import com.example.weather_service.dto.CurrentWeatherResponse;
import com.example.weather_service.dto.ForecastResponse;
import com.example.weather_service.model.City;

public interface WeatherService {

    CurrentWeatherResponse getWeather(String city);
    ForecastResponse getForecast(String city);
    List<City> getTopSearchedCities();
} 
