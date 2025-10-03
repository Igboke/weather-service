package com.example.weather_service.service;

import com.example.weather_service.dto.CurrentWeatherResponse;
import org.springframework.stereotype.Service;

@Service
public class WeatherServiceImplementation implements WeatherService{

    @Override
    public CurrentWeatherResponse getWeather(String city){

        return new CurrentWeatherResponse(city, 20.0);
    }
    
}
