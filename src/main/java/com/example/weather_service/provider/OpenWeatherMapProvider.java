package com.example.weather_service.provider;

import com.example.weather_service.service.OpenWeatherMapForecastResponse;
import com.example.weather_service.service.OpenWeatherMapResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OpenWeatherMapProvider implements WeatherProvider {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public OpenWeatherMapProvider(RestTemplate restTemplate,
                                  @Value("${openweathermap.api.base-url}") String baseUrl,
                                  @Value("${openweathermap.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    @Override
    public OpenWeatherMapResponse fetchCurrentWeather(String city) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/weather")
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();
        return restTemplate.getForObject(url, OpenWeatherMapResponse.class);
    }

    @Override
    public OpenWeatherMapForecastResponse fetchForecast(String city) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/forecast")
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();
        return restTemplate.getForObject(url, OpenWeatherMapForecastResponse.class);
    }

    
}
