package com.example.weather_service.service;

import com.example.weather_service.model.City;
import com.example.weather_service.repository.CityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherDataScheduler {

    private static final Logger log = LoggerFactory.getLogger(WeatherDataScheduler.class);
    private final CityRepository cityRepository;
    private final WeatherService weatherService;

    public WeatherDataScheduler(CityRepository cityRepository, WeatherService weatherService) {
        this.cityRepository = cityRepository;
        this.weatherService = weatherService;
    }

    
    //(fixedRate = 60000)
    @Scheduled(cron = "0 */45 * * * *") 
    public void refreshWeatherData() {
        log.info("Starting scheduled weather data refresh...");

        List<City> topCities = cityRepository.findTop10ByOrderBySearchCountDesc();

        if (topCities.isEmpty()) {
            log.info("No cities to refresh. Scheduler finished.");
            return;
        }

        log.info("Found {} cities to refresh: {}", topCities.size(), topCities.stream().map(City::getName).toList());

        for (City city : topCities) {
            try {
                log.debug("Refreshing data for {}", city.getName());
                weatherService.getWeather(city.getName());
                weatherService.getForecast(city.getName());
            } catch (Exception e) {
                log.error("Failed to refresh weather data for city: {}", city.getName(), e);
            }
        }
        log.info("Scheduled weather data refresh finished.");
    }

    
}
