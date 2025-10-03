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

@WebMvcTest(WeatherApiController.class)
public class WeatherApiControllerTests {

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

        String city = "London";
        CurrentWeatherResponse dummyResponse = new CurrentWeatherResponse(city, 10.0);
        when(weatherService.getWeather(city)).thenReturn(dummyResponse);
        
        mockMvc.perform(get("/api/weather/current?city=London"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void whenGetWeatherByCity_thenReturnsDataFromService() throws Exception{
        String city = "London";
        double temperature = 12.0;

        CurrentWeatherResponse expectedResponse = new CurrentWeatherResponse(city, temperature);

        when(weatherService.getWeather(city)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/weather/current?city=" + city))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.city").value(city))
            .andExpect(jsonPath("$.temperature").value(temperature));


    }
    
}
