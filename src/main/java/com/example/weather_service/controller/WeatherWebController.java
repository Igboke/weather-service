package com.example.weather_service.controller;

import com.example.weather_service.dto.CurrentWeatherResponse;
import com.example.weather_service.dto.ForecastResponse;
import com.example.weather_service.model.City;
import com.example.weather_service.service.WeatherService;
import com.example.weather_service.service.exception.CityNotFoundException;

import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WeatherWebController implements ErrorController {

    private final WeatherService weatherService;

    private static final Logger log = LoggerFactory.getLogger(WeatherWebController.class);

    public WeatherWebController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
    try {
        List<City> topCities = weatherService.getTopSearchedCities();
        model.addAttribute("topCities", topCities);
        log.debug("Found {} top cities to display on home page.", topCities.size());

    } catch (DataAccessException e) {
        log.error("Could not retrieve top cities from database for home page.", e);
        model.addAttribute("topCities", Collections.emptyList());
    }
        return "home";
    }

    @GetMapping("/weather")
    public String getWeatherPage(@RequestParam("city") String city, Model model) {
        log.info("Web request received for city: {}", city);

        try{CurrentWeatherResponse currentWeather = weatherService.getWeather(city);
        ForecastResponse forecast = weatherService.getForecast(city);

        if (forecast != null && forecast.forecasts() != null) {
            log.debug("Adding forecast to model with {} daily entries.", forecast.forecasts().size());
        } else {
            log.warn("Forecast object or its list is null for city: {}", city);
        }

        model.addAttribute("currentWeather", currentWeather);
        model.addAttribute("forecast", forecast);

        return "weather-details";
        }catch(CityNotFoundException e){
            log.warn("City not found for web request: {}. Redirecting to error page.", city, e);
            return "error";
        }

    }
    
    @RequestMapping("/error")
    public String handleError() {
        return "error";
    }
}   
