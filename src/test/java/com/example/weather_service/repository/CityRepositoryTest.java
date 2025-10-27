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
    void whenFindByCityName_thenReturnCityWithAllProperties() {
        
        City london = new City();
        london.setName("London");
        london.setCountry("GB");
        london.setLatitude(51.5074);
        london.setLongitude(-0.1278);
        entityManager.persistAndFlush(london);

  
        City found = cityRepository.findByName("London");

        
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo(london.getName());
        assertThat(found.getCountry()).isEqualTo(london.getCountry());
        assertThat(found.getLatitude()).isEqualTo(london.getLatitude()); 
        assertThat(found.getLongitude()).isEqualTo(london.getLongitude()); 
    }

}
