package com.example.weather_service;

import com.example.weather_service.dto.CurrentWeatherResponse;
import com.example.weather_service.dto.ForecastResponse;
import com.example.weather_service.model.City;
import com.example.weather_service.model.CurrentWeather;
import com.example.weather_service.provider.WeatherProvider;
import com.example.weather_service.repository.CityRepository;
import com.example.weather_service.repository.CurrentWeatherRepository;
import com.example.weather_service.repository.ForecastRepository;
import com.example.weather_service.service.OpenWeatherMapForecastResponse;
import com.example.weather_service.service.OpenWeatherMapResponse;
import com.example.weather_service.service.WeatherServiceImplementation;
import com.example.weather_service.service.exception.CityNotFoundException;
import com.example.weather_service.service.exception.WeatherProviderException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceImplementationTest {

    @Mock
    private WeatherProvider weatherProvider;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CurrentWeatherRepository currentWeatherRepository;

    @Mock
    private ForecastRepository forecastRepository;

    private WeatherServiceImplementation weatherService;

    @BeforeEach
    void setUp() {
    weatherService = new WeatherServiceImplementation(weatherProvider, cityRepository, currentWeatherRepository, forecastRepository);
    }

    @Test
    void getWeather_shouldReturnCorrectlyMappedData() {

        String city = "Berlin";
        String country = "DE";
        double expectedTemp = 8.7;

        OpenWeatherMapResponse fakeApiResponse = new OpenWeatherMapResponse();
        fakeApiResponse.setCity(city);

        OpenWeatherMapResponse.Main main = new OpenWeatherMapResponse.Main();
        main.setTemperature(expectedTemp);
        fakeApiResponse.setMain(main);

        OpenWeatherMapResponse.Sys sys = new OpenWeatherMapResponse.Sys();
        sys.setCountry(country);
        fakeApiResponse.setSys(sys);

        fakeApiResponse.setCoord(new OpenWeatherMapResponse.Coord());
        fakeApiResponse.setWind(new OpenWeatherMapResponse.Wind());
        fakeApiResponse.setWeather(List.of(new OpenWeatherMapResponse.Weather()));
        
        when(weatherProvider.fetchCurrentWeather(city)).thenReturn(fakeApiResponse);
        
        CurrentWeatherResponse actualResponse = weatherService.getWeather(city);
        
        assertEquals(city, actualResponse.city());
        assertEquals(expectedTemp, actualResponse.temperature());
}
    @Test
    void whenCityNotFoundOnApi_thenThrowsCityNotFoundException(){
        String invalidCity = "InvalidCity";

        when(weatherProvider.fetchCurrentWeather(invalidCity))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(CityNotFoundException.class, () -> {
        weatherService.getWeather(invalidCity);});
    }
            
    @Test
    void whenGetWeatherSucceeds_thenCityIsSavedToDatabase() {

        String cityName = "Paris";
        String country = "FR";
        double temperature = 23.0;
        OpenWeatherMapResponse fakeApiResponse = new OpenWeatherMapResponse();
        fakeApiResponse.setCity(cityName);
 
        OpenWeatherMapResponse.Main main = new OpenWeatherMapResponse.Main();
        main.setTemperature(temperature);
        fakeApiResponse.setMain(main);

        OpenWeatherMapResponse.Sys sys = new OpenWeatherMapResponse.Sys();
        sys.setCountry(country);
        fakeApiResponse.setSys(sys);

        fakeApiResponse.setCoord(new OpenWeatherMapResponse.Coord());
        fakeApiResponse.setWind(new OpenWeatherMapResponse.Wind());
        fakeApiResponse.setWeather(List.of(new OpenWeatherMapResponse.Weather()));

        when(weatherProvider.fetchCurrentWeather(cityName)).thenReturn(fakeApiResponse);


        weatherService.getWeather(cityName);

        ArgumentCaptor<City> cityArgumentCaptor = ArgumentCaptor.forClass(City.class);
        
        verify(cityRepository, times(1)).save(cityArgumentCaptor.capture());
        

        City savedCity = cityArgumentCaptor.getValue();
        assertEquals(cityName, savedCity.getName());
    }

    @Test
    void whenGetWeather_thenSavesCityAndCurrentWeather(){
        String cityName = "Paris";
        String country = "FR";
        double temp = 27.0;
        int humidity = 15;
        int pressure =10;
        long sunrise = 393847474;
        long sunset = 484439383;
        
        OpenWeatherMapResponse fakeApiResponse = new OpenWeatherMapResponse();
        fakeApiResponse.setCity(cityName);
        
        OpenWeatherMapResponse.Main main = new OpenWeatherMapResponse.Main();
        main.setTemperature(temp);
        main.setHumidity(humidity);
        main.setPressure(pressure);
        fakeApiResponse.setMain(main);

        OpenWeatherMapResponse.Sys sys = new OpenWeatherMapResponse.Sys();
        sys.setCountry(country);
        sys.setSunrise(sunrise);
        sys.setSunset(sunset);
        fakeApiResponse.setSys(sys);

        fakeApiResponse.setCoord(new OpenWeatherMapResponse.Coord());
        fakeApiResponse.setWind(new OpenWeatherMapResponse.Wind());
        fakeApiResponse.setWeather(List.of(new OpenWeatherMapResponse.Weather()));

        when(weatherProvider.fetchCurrentWeather(cityName)).thenReturn(fakeApiResponse);

        when(cityRepository.findByName(cityName)).thenReturn(null);

        weatherService.getWeather(cityName);
        
        ArgumentCaptor<City> cityCaptor = ArgumentCaptor.forClass(City.class);

        verify(cityRepository, times(1)).save(cityCaptor.capture());

        City savedCity = cityCaptor.getValue();
        assertThat(savedCity.getName()).isEqualTo(cityName);
        assertThat(savedCity.getCountry()).isEqualTo(country);
        assertThat(savedCity.getSearchCount()).isEqualTo(1);
        assertThat(savedCity.getLastSearched()).isNotNull();

        
        CurrentWeather savedWeather = savedCity.getCurrentWeather();
        assertThat(savedWeather).isNotNull();
        assertThat(savedWeather.getTemperature()).isEqualTo(temp);
        assertThat(savedWeather.getCity()).isEqualTo(savedCity);
    }

    @Test
    void getForecast_shouldProcessApiDataAndReturnDailyForecasts() {

    String city = "London";
    String country = "GB";


    OpenWeatherMapForecastResponse fakeApiResponse = new OpenWeatherMapForecastResponse();
    OpenWeatherMapForecastResponse.CityInfo cityInfo = new OpenWeatherMapForecastResponse.CityInfo();
    cityInfo.setName(city);
    cityInfo.setCountry(country);
    OpenWeatherMapForecastResponse.CityInfo.Coord coord = new OpenWeatherMapForecastResponse.CityInfo.Coord();
    coord.setLat(5.0);
    coord.setLon(7.0);
    cityInfo.setCoord(coord);
    fakeApiResponse.setCityInfo(cityInfo);

    OpenWeatherMapForecastResponse.ForecastItem item1 = new OpenWeatherMapForecastResponse.ForecastItem();
    item1.setDateTimeText("2025-10-06 12:00:00");
    item1.setMain(new OpenWeatherMapResponse.Main());
    item1.getMain().setTemperature(15.0);
    item1.setWeather(List.of(new OpenWeatherMapResponse.Weather()));
    item1.getWeather().get(0).setDescription("Cloudy");

    OpenWeatherMapResponse.Wind wind = new OpenWeatherMapResponse.Wind();
    wind.setWindSpeed(5.0f);
    wind.setWindDirection(180);
    item1.setWind(wind);

    OpenWeatherMapForecastResponse.ForecastItem item2 = new OpenWeatherMapForecastResponse.ForecastItem();
    item2.setDateTimeText("2025-10-06 21:00:00");
    item2.setMain(new OpenWeatherMapResponse.Main());
    item2.getMain().setTemperature(8.0);
    item2.setWeather(List.of(new OpenWeatherMapResponse.Weather()));
    OpenWeatherMapResponse.Wind wind2 = new OpenWeatherMapResponse.Wind();
    wind2.setWindSpeed(5.0f);
    wind2.setWindDirection(180);
    item2.setWind(wind2);

    
    OpenWeatherMapForecastResponse.ForecastItem item3 = new OpenWeatherMapForecastResponse.ForecastItem();
    item3.setDateTimeText("2025-10-07 15:00:00");
    item3.setMain(new OpenWeatherMapResponse.Main());
    item3.getMain().setTemperature(16.0);
    item3.setWeather(List.of(new OpenWeatherMapResponse.Weather()));
    item3.getWeather().get(0).setDescription("Sunny");
    OpenWeatherMapResponse.Wind wind3 = new OpenWeatherMapResponse.Wind();
    wind3.setWindSpeed(5.0f);
    wind3.setWindDirection(180);
    item3.setWind(wind3);

    fakeApiResponse.setList(List.of(item1, item2, item3));

    
    when(weatherProvider.fetchForecast(city)).thenReturn(fakeApiResponse);

    
    ForecastResponse actualResponse = weatherService.getForecast(city);

    assertThat(actualResponse).isNotNull();
    assertThat(actualResponse.city()).isEqualTo(city);
    assertThat(actualResponse.country()).isEqualTo(country);
    assertThat(actualResponse.forecasts()).hasSize(2);
    assertThat(actualResponse.forecasts().get(0).date()).isEqualTo("2025-10-06");
    assertThat(actualResponse.forecasts().get(0).maxTemperature()).isEqualTo(15.0);
    assertThat(actualResponse.forecasts().get(0).minTemperature()).isEqualTo(8.0);
    assertThat(actualResponse.forecasts().get(0).description()).isEqualTo("Cloudy");
    assertThat(actualResponse.forecasts().get(1).date()).isEqualTo("2025-10-07");
    assertThat(actualResponse.forecasts().get(1).maxTemperature()).isEqualTo(16.0);
    assertThat(actualResponse.forecasts()).hasSize(2);

    ArgumentCaptor<City> cityCaptor = ArgumentCaptor.forClass(City.class);

    verify(cityRepository, times(1)).save(cityCaptor.capture());

    City savedCity = cityCaptor.getValue();
    assertThat(savedCity.getForecasts()).isNotNull();
    assertThat(savedCity.getForecasts()).hasSize(2);
    assertThat(savedCity.getForecasts().get(0).getTemperature()).isEqualTo(15.0);
    }

    @Test
    void whenProviderFails_thenGetWeatherThrowsException() {
    String city = "Abuja";
    when(weatherProvider.fetchCurrentWeather(city))
            .thenThrow(new WeatherProviderException("API is down", null));

    assertThrows(WeatherProviderException.class, () -> {
        weatherService.getWeather(city);
    });

}
}
