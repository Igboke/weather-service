package com.example.weather_service;

import com.example.weather_service.dto.CurrentWeatherResponse;
import com.example.weather_service.service.OpenWeatherMapResponse;
import com.example.weather_service.service.WeatherServiceImplementation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceImplementationTest {

    @Mock
    private RestTemplate restTemplate;

    private WeatherServiceImplementation weatherService;

    @BeforeEach
    void setUp() {
        String fakeApiUrl = "https://fakeapi.com/weather";
        String fakeApiKey = "fakekey";
        weatherService = new WeatherServiceImplementation(restTemplate, fakeApiUrl, fakeApiKey);
    }

    @Test
    void getWeather_shouldReturnCorrectlyMappedData() {

        String city = "Berlin";
        double expectedTemp = 8.7;
        OpenWeatherMapResponse fakeApiResponse = new OpenWeatherMapResponse();
        fakeApiResponse.setCity(city);
        OpenWeatherMapResponse.Main main = new OpenWeatherMapResponse.Main();
        main.setTemperature(expectedTemp);
        fakeApiResponse.setMain(main);
        
        when(restTemplate.getForObject(anyString(), eq(OpenWeatherMapResponse.class)))
            .thenReturn(fakeApiResponse);
        
        CurrentWeatherResponse actualResponse = weatherService.getWeather(city);
        
        assertEquals(city, actualResponse.getCity());
        assertEquals(expectedTemp, actualResponse.getTemperature());
}

}
