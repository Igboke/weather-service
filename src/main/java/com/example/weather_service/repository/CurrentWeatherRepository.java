package com.example.weather_service.repository;

import com.example.weather_service.model.City;
import com.example.weather_service.model.CurrentWeather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentWeatherRepository extends JpaRepository<CurrentWeather, Long> {
    CurrentWeather findByCity(City city);
}
