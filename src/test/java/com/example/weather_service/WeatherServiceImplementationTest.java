package com.example.weather_service;

import com.example.weather_service.dto.CurrentWeatherResponse;
import com.example.weather_service.dto.ForecastResponse;
import com.example.weather_service.model.City;
import com.example.weather_service.repository.CityRepository;
import com.example.weather_service.service.OpenWeatherMapForecastResponse;
import com.example.weather_service.service.OpenWeatherMapResponse;
import com.example.weather_service.service.WeatherServiceImplementation;
import com.example.weather_service.service.exception.CityNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceImplementationTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CityRepository cityRepository;

    private WeatherServiceImplementation weatherService;

    @BeforeEach
    void setUp() {
        String fakeApiUrl = "https://fakeapi.com/weather";
        String fakeApiKey = "fakekey";
        weatherService = new WeatherServiceImplementation(restTemplate, cityRepository, fakeApiUrl, fakeApiKey);
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
    @Test
    void whenCityNotFoundOnApi_thenThrowsCityNotFoundException(){
        String invalidCity = "InvalidCity";

        when(restTemplate.getForObject(anyString(), eq(OpenWeatherMapResponse.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(CityNotFoundException.class, () -> {
        weatherService.getWeather(invalidCity);});
    }

    @Test
    void whenGetWeatherIsCalledMultipleTimes_thenApiIsCalledOnlyOnce(){
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

    @Test
    void whenGetWeatherSucceeds_thenCityIsSavedToDatabase() {

        String cityName = "Paris";
        OpenWeatherMapResponse fakeApiResponse = new OpenWeatherMapResponse();
        fakeApiResponse.setCity(cityName);
 
        OpenWeatherMapResponse.Main main = new OpenWeatherMapResponse.Main();
        main.setTemperature(22.0);
        fakeApiResponse.setMain(main);

        when(restTemplate.getForObject(anyString(), eq(OpenWeatherMapResponse.class)))
                .thenReturn(fakeApiResponse);


        weatherService.getWeather(cityName);

        ArgumentCaptor<City> cityArgumentCaptor = ArgumentCaptor.forClass(City.class);
        
        verify(cityRepository, times(1)).save(cityArgumentCaptor.capture());
        

        City savedCity = cityArgumentCaptor.getValue();
        assertEquals(cityName, savedCity.getName());
    }

    @Test
    void getForecast_shouldProcessApiDataAndReturnDailyForecasts() {

    String city = "London";
    String country = "GB";


    OpenWeatherMapForecastResponse fakeApiResponse = new OpenWeatherMapForecastResponse();
    OpenWeatherMapForecastResponse.CityInfo cityInfo = new OpenWeatherMapForecastResponse.CityInfo();
    cityInfo.setName(city);
    cityInfo.setCountry(country);
    fakeApiResponse.setCityInfo(cityInfo);

    OpenWeatherMapForecastResponse.ForecastItem item1 = new OpenWeatherMapForecastResponse.ForecastItem();
    item1.setDateTimeText("2025-10-06 12:00:00");
    item1.setMain(new OpenWeatherMapResponse.Main());
    item1.getMain().setTemperature(15.0);
    item1.setWeather(List.of(new OpenWeatherMapResponse.Weather()));
    item1.getWeather().get(0).setDescription("Cloudy");

    OpenWeatherMapForecastResponse.ForecastItem item2 = new OpenWeatherMapForecastResponse.ForecastItem();
    item2.setDateTimeText("2025-10-06 21:00:00");
    item2.setMain(new OpenWeatherMapResponse.Main());
    item2.getMain().setTemperature(8.0);

    
    OpenWeatherMapForecastResponse.ForecastItem item3 = new OpenWeatherMapForecastResponse.ForecastItem();
    item3.setDateTimeText("2025-10-07 15:00:00");
    item3.setMain(new OpenWeatherMapResponse.Main());
    item3.getMain().setTemperature(16.0);
    item3.setWeather(List.of(new OpenWeatherMapResponse.Weather()));
    item3.getWeather().get(0).setDescription("Sunny");

    fakeApiResponse.setList(List.of(item1, item2, item3));

    
    when(restTemplate.getForObject(anyString(), eq(OpenWeatherMapForecastResponse.class)))
            .thenReturn(fakeApiResponse);

    
    ForecastResponse actualResponse = weatherService.getForecast(city);

    assertThat(actualResponse).isNotNull();
    assertThat(actualResponse.getCity()).isEqualTo(city);
    assertThat(actualResponse.getCountry()).isEqualTo(country);
    assertThat(actualResponse.getForecasts()).hasSize(2);
    assertThat(actualResponse.getForecasts().get(0).getDate()).isEqualTo("2025-10-06");
    assertThat(actualResponse.getForecasts().get(0).getMaxTemperature()).isEqualTo(15.0);
    assertThat(actualResponse.getForecasts().get(0).getMinTemperature()).isEqualTo(8.0);
    assertThat(actualResponse.getForecasts().get(0).getDescription()).isEqualTo("Cloudy");
    assertThat(actualResponse.getForecasts().get(1).getDate()).isEqualTo("2025-10-07");
    assertThat(actualResponse.getForecasts().get(1).getMaxTemperature()).isEqualTo(16.0);
}

}
