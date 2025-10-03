package com.example.weather_service.controller;

import com.example.weather_service.service.WeatherService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.weather_service.dto.CurrentWeatherResponse;

@RestController
public class WeatherApiController {

    private final WeatherService weatherService;

    public WeatherApiController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/api/weather/current")
    public CurrentWeatherResponse getWeatherByCity(@RequestParam("city")String city){

        return weatherService.getWeather(city);
    }

    
}
