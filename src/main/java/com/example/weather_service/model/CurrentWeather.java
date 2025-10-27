package com.example.weather_service.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "current_weather")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "city")
public class CurrentWeather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double temperature;
    private int humidity;
    private double windSpeed;
    private float windDirection;
    private int pressure;
    private String weatherMain;
    private String weatherDesc;
    private String sunrise;
    private String sunset;
    private LocalDateTime lastUpdated;

    @OneToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;
}
