package com.example.weather_service.provider;

import com.example.weather_service.service.OpenWeatherMapForecastResponse;
import com.example.weather_service.service.OpenWeatherMapResponse;

public interface WeatherProvider {
    OpenWeatherMapResponse fetchCurrentWeather(String city);
    OpenWeatherMapForecastResponse fetchForecast(String city);
}
