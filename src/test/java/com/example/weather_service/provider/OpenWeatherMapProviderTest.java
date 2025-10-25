package com.example.weather_service.provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.weather_service.service.OpenWeatherMapResponse;
import com.example.weather_service.service.exception.WeatherProviderException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OpenWeatherMapProviderTest {

    @Mock
    private RestTemplate restTemplate;

    private OpenWeatherMapProvider provider;

    @BeforeEach
    void setUp() {
        provider = new OpenWeatherMapProvider(restTemplate, "https://fake.api", "fakekey");
    }

    @Test
    void whenApiReturnsServerError_thenThrowsWeatherProviderException() {
        String city = "London";
        when(restTemplate.getForObject(anyString(), eq(OpenWeatherMapResponse.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(WeatherProviderException.class, () -> {
            provider.fetchCurrentWeather(city);
        });
    }
    
}
