package com.example.weather_service;

import org.springframework.beans.factory.annotation.Autowired; 
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.weather_service.controller.WeatherApiController;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(WeatherApiController.class)
public class WeatherApiControllerTests {

    @Autowired
    private MockMvc mockMvc;
    //method for getting city whenGetWeatherByCity_thenReturns200Ok.

    @Test
    void whenGetWeatherByCity_thenReturns200Ok () throws Exception{
        mockMvc.perform(get("/api/weather/current?city=London")).andExpect(status().isOk());

    }
    
}
