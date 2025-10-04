package com.example.weather_service.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.weather_service.model.City;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
public class CityRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CityRepository cityRepository;

    @Test
    void whenFindByCityName_thenReturnCity() {
        
        City london = new City("London");
        entityManager.persistAndFlush(london);

  
        City found = cityRepository.findByCityName("London");

        
        assertThat(found).isNotNull();
        assertThat(found.getCityName()).isEqualTo(london.getCityName());
    }

}
