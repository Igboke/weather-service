package com.example.weather_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class WeatherApiController {

    @GetMapping("/api/weather/current")
    public String getWeatherByCity(@RequestParam("city")String city){

        return "Request received for " + city;
    }

    
}
