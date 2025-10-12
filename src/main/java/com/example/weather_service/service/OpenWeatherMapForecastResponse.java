package com.example.weather_service.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherMapForecastResponse {

    @JsonProperty("list")
    private List<ForecastItem> list;

    @JsonProperty("city")
    private CityInfo cityInfo;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastItem {
        @JsonProperty("dt_txt")
        private String dateTimeText;

        @JsonProperty("main")
        private OpenWeatherMapResponse.Main main;

        @JsonProperty("weather")
        private List<OpenWeatherMapResponse.Weather> weather;

        @JsonProperty("wind")
        private OpenWeatherMapResponse.Wind wind;

        @JsonProperty("pop")
        private float pop;
    
        @JsonProperty("rain")
        private Rain rain;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class  Rain {
            @JsonProperty("3h")
            private Double rainMm;
            
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CityInfo {
        @JsonProperty("name")
        private String name;

        @JsonProperty("country")
        private String country;

        @JsonProperty("coord")
        private Coord coord;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Coord{

            @JsonProperty("lon")
            private double lon;

            @JsonProperty("lat")
            private double lat;

        }
    }

}
