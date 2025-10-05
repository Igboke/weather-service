package com.example.weather_service.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "current_weather")
@Data
@NoArgsConstructor
public class CurrentWeather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double temperature;
    private int humidity;
    private double windSpeed;
    private int windDirection;
    private int pressure;
    private String weatherMain;
    private String weatherDesc;
    private LocalDateTime sunrise;
    private LocalDateTime sunset;
    private LocalDateTime lastUpdated;

    @OneToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;
}
