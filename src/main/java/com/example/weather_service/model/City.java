package com.example.weather_service.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cities")
@Data
@NoArgsConstructor
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String country;

    private double latitude;

    private double longitude;

    private LocalDateTime lastSearched;

    private int searchCount = 0;

    @OneToOne(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CurrentWeather currentWeather;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Forecast> forecasts = new ArrayList<>();

    public Optional<Forecast> findForecastForDate(LocalDate date) {
    return this.forecasts.stream()
            .filter(f -> f.getForecastDate().equals(date))
            .findFirst();
}
    
}
