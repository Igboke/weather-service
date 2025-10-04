package com.example.weather_service.service;

import com.example.weather_service.dto.CurrentWeatherResponse;
import com.example.weather_service.service.exception.CityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WeatherServiceImplementation implements WeatherService{

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;

    public WeatherServiceImplementation (RestTemplate restTemplate,
                              @Value("${openweathermap.api.url}") String apiUrl,
                              @Value("${openweathermap.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    @Override
    public CurrentWeatherResponse getWeather(String city){
        String url = UriComponentsBuilder.fromUriString(apiUrl)
            .queryParam("q", city)
            .queryParam("appid", apiKey)
            .queryParam("units", "metric")
            .toUriString();

        
        try{
            OpenWeatherMapResponse response = restTemplate.getForObject(url, OpenWeatherMapResponse.class);

            if (response != null && response.getMain() != null) {
                return new CurrentWeatherResponse(response.getCity(), response.getMain().getTemperature());
            } else{
                throw new CityNotFoundException("Weather data not available for city: " + city);
            }
        }catch (HttpClientErrorException e){
            throw new CityNotFoundException("City not found: " + city);   
        }
    }
    
}
