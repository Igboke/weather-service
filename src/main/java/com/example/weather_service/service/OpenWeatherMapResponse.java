package com.example.weather_service.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

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

        @JsonProperty("humidity")
        private int humidity;

        @JsonProperty("pressure")
        private int pressure; 
    }

    @JsonProperty("sys")
    private Sys sys;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sys {
        @JsonProperty("country")
        private String country;

        @JsonProperty("sunrise")
        private long sunrise;

        @JsonProperty("sunset")
        private long sunset;
        
    }

    @JsonProperty("dt")
    private long dt;

    @JsonProperty("timezone")
    private int timezone;

    @JsonProperty("wind")
    private Wind wind;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        
        @JsonProperty("speed")
        private float windSpeed;

        @JsonProperty("deg")
        private float windDirection;   
    }

    @JsonProperty("weather")
    private List <Weather> weather;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {

        @JsonProperty("main")
        private String conditions;

        @JsonProperty("description")
        private String description;
    }

}
