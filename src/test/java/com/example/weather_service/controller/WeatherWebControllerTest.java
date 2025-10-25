package com.example.weather_service.controller;

import org.junit.jupiter.api.Test;
import com.example.weather_service.dto.CurrentWeatherResponse;
import com.example.weather_service.dto.DailyForecast;
import com.example.weather_service.dto.ForecastResponse;
import com.example.weather_service.model.City;
import com.example.weather_service.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@WebMvcTest(WeatherWebController.class)
public class WeatherWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;


    @Test
    void whenGetWeatherPageWithCity_thenReturnsViewWithData() throws Exception {
        String city = "London";
        double temperature =10.0;
        String country = "GB";
        String timestamp ="2025-10-06T12:00:00";
        String sunrise="2025-10-06T12:00:00";
        String sunset="2025-10-06T12:00:00";
        int humidity= 80;
        int pressure=1012;
        float windSpeed=101.0f;
        float windDirection=80.0f;
        String conditions = "clear sky";
        String description ="cloudy";

        String date="2025-10-08T12:00:00";
        double maxTemperature=50.0;
        double minTemperature=50.0;
        float rainProbability=50.0f;
        double rainMillimeter=50.0;

        CurrentWeatherResponse fakeWeather = new CurrentWeatherResponse(
                city, temperature, country, timestamp, sunrise,sunset, humidity, pressure,windSpeed,windDirection, conditions, description,"Icon"
        );
        when(weatherService.getWeather(city)).thenReturn(fakeWeather);

        DailyForecast day1 = new DailyForecast(date, maxTemperature, minTemperature, description,conditions,humidity,pressure,windSpeed,windDirection,rainProbability,rainMillimeter,"Icon");

        ForecastResponse fakeForecast = new ForecastResponse(city, "US", List.of(day1));
            when(weatherService.getForecast(city)).thenReturn(fakeForecast);
            
        mockMvc.perform(get("/weather").param("city", city))
                .andExpect(status().isOk())
                .andExpect(view().name("weather-details"))
                .andExpect(model().attributeExists("currentWeather"))
                .andExpect(model().attributeExists("forecast"));
    }

    @Test
    void whenGetHomePage_thenReturnsHomeView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void whenErrorOccurs_thenReturnsCustomErrorView() throws Exception {
    mockMvc.perform(get("/error"))
            .andExpect(status().isOk())
            .andExpect(view().name("error"));
    }

    @Test
    void whenGetHomePage_thenReturnsViewWithTopCities() throws Exception {
        City london = new City();
        london.setName("London");
        List<City> fakeTopCities = List.of(london);

        when(weatherService.getTopSearchedCities()).thenReturn(fakeTopCities);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("topCities"))
                .andExpect(model().attribute("topCities", fakeTopCities));
    }
}
