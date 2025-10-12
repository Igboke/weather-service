package com.example.weather_service.repository;

import com.example.weather_service.model.City;
import com.example.weather_service.model.Forecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast, Long> {
    List<Forecast> findByCity(City city);
}
