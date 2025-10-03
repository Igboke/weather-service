package com.example.weather_service.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true) 
@Data
public class OpenWeatherMapResponse {
    @JsonProperty("name")
    private String city;
    
    @JsonProperty("main")
    private Main main;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        @JsonProperty("temp")
        private double temperature;
    }
}
