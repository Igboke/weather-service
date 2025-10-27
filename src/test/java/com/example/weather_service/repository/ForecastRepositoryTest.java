package com.example.weather_service.repository;

import com.example.weather_service.model.City;
import com.example.weather_service.model.Forecast;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ForecastRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ForecastRepository forecastRepository;

    @Test
    void whenFindByCity_thenReturnListOfForecasts() {
        City berlin = new City();
        berlin.setName("Berlin");
        berlin.setCountry("DE");
        berlin.setLatitude(52.5);
        berlin.setLongitude(13.4);

        Forecast forecast1 = new Forecast();
        forecast1.setForecastDate(LocalDate.now().plusDays(1));
        forecast1.setTemperature(15.0);
        forecast1.setCity(berlin);

        Forecast forecast2 = new Forecast();
        forecast2.setForecastDate(LocalDate.now().plusDays(2));
        forecast2.setTemperature(16.5);
        forecast2.setCity(berlin); 


        berlin.getForecasts().add(forecast1);
        berlin.getForecasts().add(forecast2);

        entityManager.persistAndFlush(berlin);

        List<Forecast> foundForecasts = forecastRepository.findByCity(berlin);

        assertThat(foundForecasts).isNotNull();
        assertThat(foundForecasts).hasSize(2);
        assertThat(foundForecasts.get(0).getTemperature()).isEqualTo(15.0);
    }
}