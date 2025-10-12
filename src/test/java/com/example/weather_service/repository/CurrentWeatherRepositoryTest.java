package com.example.weather_service.repository;

import com.example.weather_service.model.City;
import com.example.weather_service.model.CurrentWeather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

@DataJpaTest
public class CurrentWeatherRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CurrentWeatherRepository currentWeatherRepository;

    @Test
    void whenFindByCity_thenReturnCurrentWeather() {

        City london = new City();
        london.setName("London");
        london.setCountry("GB");
        london.setLatitude(51.5);
        london.setLongitude(-0.12);
        
        CurrentWeather weather = new CurrentWeather();
        weather.setTemperature(15.5);
        weather.setHumidity(80);
        weather.setWeatherDesc("clear sky");
        weather.setWindDirection(10);
        weather.setWindSpeed(19.8);
        weather.setPressure(1012);
        weather.setLastUpdated(LocalDateTime.now());

        london.setCurrentWeather(weather);

        weather.setCity(london);

        entityManager.persistAndFlush(london);

        CurrentWeather found = currentWeatherRepository.findByCity(london);

        assertThat(found).isNotNull();
        assertThat(found.getTemperature()).isEqualTo(15.5);
        assertThat(found.getCity().getName()).isEqualTo("London");
    }
    
}
