package com.example.weather_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.weather_service.model.City;

@Repository
public interface CityRepository extends JpaRepository<City, Long>{

    City findByName(String cityName);

    
}
