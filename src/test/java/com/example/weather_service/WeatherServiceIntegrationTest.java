package com.example.weather_service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import com.example.weather_service.service.OpenWeatherMapResponse;
import com.example.weather_service.service.WeatherService;

@SpringBootTest
public class WeatherServiceIntegrationTest {
    
    @Autowired
    private WeatherService weatherService; 

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void whenGetWeatherIsCalledMultipleTimes_thenApiIsCalledOnlyOnce() {

        String city = "Tokyo";
        double temperature = 25.0;

        OpenWeatherMapResponse fakeApiResponse = new OpenWeatherMapResponse();
        fakeApiResponse.setCity(city);
        OpenWeatherMapResponse.Main main = new OpenWeatherMapResponse.Main();
        main.setTemperature(temperature);
        fakeApiResponse.setMain(main);

        when(restTemplate.getForObject(anyString(), eq(OpenWeatherMapResponse.class)))
                .thenReturn(fakeApiResponse);


        weatherService.getWeather(city);
        weatherService.getWeather(city);

        verify(restTemplate, times(1)).getForObject(anyString(), eq(OpenWeatherMapResponse.class));
    }
}
