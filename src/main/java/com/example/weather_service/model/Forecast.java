package com.example.weather_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "forecasts")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "city")
public class Forecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate forecastDate;
    private double temperature;
    private double windSpeed;
    private float windDirection;
    private int pressure;
    private double humidity;
    private String weatherMain;
    private String weatherDesc;
    private double rainVolume;
    private float probability;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;
    
}
