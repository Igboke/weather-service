package com.example.weather_service.repository;

import com.example.weather_service.model.City;
import com.example.weather_service.model.Forecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast, Long> {
    List<Forecast> findByCity(City city);
    Optional<Forecast> findByCityAndForecastDate(City city, LocalDate date);
}
