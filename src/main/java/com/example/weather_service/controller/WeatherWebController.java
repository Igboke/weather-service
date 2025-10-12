package com.example.weather_service.controller;

import com.example.weather_service.dto.CurrentWeatherResponse;
import com.example.weather_service.dto.ForecastResponse;
import com.example.weather_service.service.WeatherService;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WeatherWebController implements ErrorController {

    private final WeatherService weatherService;
    private static final Logger log = LoggerFactory.getLogger(WeatherWebController.class);

    public WeatherWebController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/")
    public String getHomePage() {
        return "home";
    }

    @GetMapping("/weather")
    public String getWeatherPage(@RequestParam("city") String city, Model model) {
        log.info("Web request received for city: {}", city);

        CurrentWeatherResponse currentWeather = weatherService.getWeather(city);
        ForecastResponse forecast = weatherService.getForecast(city);

        if (forecast != null && forecast.getForecasts() != null) {
            log.debug("Adding forecast to model with {} daily entries.", forecast.getForecasts().size());
        } else {
            log.warn("Forecast object or its list is null for city: {}", city);
        }

        model.addAttribute("currentWeather", currentWeather);
        model.addAttribute("forecast", forecast);

        return "weather-details";

    }
    
    @RequestMapping("/error")
    public String handleError() {
        // This method simply returns the name of our custom error view.
        return "error";
    }
}   
