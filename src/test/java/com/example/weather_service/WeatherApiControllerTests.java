package com.example.weather_service;

import org.springframework.beans.factory.annotation.Autowired; 
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.weather_service.controller.WeatherApiController;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import com.example.weather_service.dto.CurrentWeatherResponse;
import com.example.weather_service.service.WeatherService;
import com.example.weather_service.service.exception.CityNotFoundException;

import com.example.weather_service.dto.DailyForecast;
import com.example.weather_service.dto.ForecastResponse;
import java.util.List;

@WebMvcTest(WeatherApiController.class)
public class WeatherApiControllerTests {

    String city = "London";
    double temperature = 12.0;
    String country = "GB";
    String timestamp="";
    String sunrise="";
    String sunset="";
    int humidity=1;
    int pressure=1;
    float windSpeed=1.0f;
    float windDirection=1.0f;
    String conditions="";
    String description="";

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private WeatherService weatherService;

    @Test
    void whenGetWeatherByCity_thenReturns200Ok () throws Exception{
        mockMvc.perform(get("/api/weather/current?city=Lagos")).andExpect(status().isOk());
    }

    @Test
    void whenGetWeatherByCity_thenReturnsJsonResponse() throws Exception{

        CurrentWeatherResponse dummyResponse = new CurrentWeatherResponse(city, 10.0,country,timestamp,sunrise,sunset,humidity,pressure
        ,windSpeed,windDirection,conditions,description,"icon");
        when(weatherService.getWeather(city)).thenReturn(dummyResponse);
        
        mockMvc.perform(get("/api/weather/current?city=London"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void whenGetWeatherByCity_thenReturnsDataFromService() throws Exception{

        CurrentWeatherResponse expectedResponse = new CurrentWeatherResponse(city, temperature,country
        ,timestamp,sunrise,sunset,humidity,pressure,windSpeed,windDirection,conditions,description,"icon");

        when(weatherService.getWeather(city)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/weather/current?city=" + city))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.city").value(city))
            .andExpect(jsonPath("$.temperature").value(temperature));


    }

    @Test
    void whenGetWeatherForInvalidCity_thenReturns404NotFound() throws Exception{
        String invalidCity = "InvalidCity";
        when(weatherService.getWeather(invalidCity))
            .thenThrow(new CityNotFoundException("City not found: " + invalidCity));
        mockMvc.perform(get("/api/weather/current?city=" + invalidCity))
            .andExpect(status().isNotFound());
    }

    @Test
    void whenGetForecastByCity_thenReturnsForecastList() throws Exception {
  
    String date = "2025-10-06";
    double maxTemperature = 15.0;
    double minTemperature=15.0;
    String description = "Cloudy";
    String conditions="Cloudy";
    double humidity=10.0;
    int pressure=2000;
    double windSpeed=10.0;
    float windDirection=10.0f;
    float rainProbability=15.0f;
    double rainMillimeter=20.0;
    
    DailyForecast day1 = new DailyForecast(date, maxTemperature, minTemperature, description,conditions,humidity,pressure
    ,windSpeed,windDirection,rainProbability,rainMillimeter,"Icon");
    DailyForecast day2 = new DailyForecast(date,maxTemperature,minTemperature,description,conditions,humidity,pressure,windSpeed,windDirection,rainProbability,rainMillimeter,"icon");
    
    ForecastResponse expectedResponse = new ForecastResponse(city, country, List.of(day1, day2));

    when(weatherService.getForecast(city)).thenReturn(expectedResponse);

    mockMvc.perform(get("/api/weather/forecast?city=" + city))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.city").value(city))
            .andExpect(jsonPath("$.forecasts.length()").value(2))
            .andExpect(jsonPath("$.forecasts[0].date").value(date))
            .andExpect(jsonPath("$.forecasts[0].maxTemperature").value(15.0));
    }
    
}
