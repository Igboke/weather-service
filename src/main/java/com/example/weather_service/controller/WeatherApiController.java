package com.example.weather_service.controller;

import com.example.weather_service.service.WeatherService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.weather_service.dto.CurrentWeatherResponse;
import com.example.weather_service.dto.ForecastResponse;

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
    
    @GetMapping("/api/weather/forecast")
    public ForecastResponse getForecastByCity(@RequestParam("city") String city) {
        return weatherService.getForecast(city);
    }

    
}
