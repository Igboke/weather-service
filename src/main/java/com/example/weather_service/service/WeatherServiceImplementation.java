package com.example.weather_service.service;

import com.example.weather_service.dto.CurrentWeatherResponse;
import com.example.weather_service.dto.DailyForecast;
import com.example.weather_service.dto.ForecastResponse;
import com.example.weather_service.model.City;
import com.example.weather_service.model.CurrentWeather;
import com.example.weather_service.model.Forecast;
import com.example.weather_service.repository.CityRepository;
import com.example.weather_service.repository.CurrentWeatherRepository;
import com.example.weather_service.repository.ForecastRepository;
import com.example.weather_service.service.exception.CityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class WeatherServiceImplementation implements WeatherService{

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;
    private final CityRepository cityRepository;
    private final CurrentWeatherRepository currentWeatherRepository;
    private final ForecastRepository forecastRepository;
    private static final Logger log = LoggerFactory.getLogger(WeatherServiceImplementation.class);

    public WeatherServiceImplementation (RestTemplate restTemplate,
                                        CityRepository cityRepository,
                                        CurrentWeatherRepository currentWeatherRepository,
                                        ForecastRepository forecastRepository,
                                        @Value("${openweathermap.api.base-url}") String apiUrl,
                                        @Value("${openweathermap.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.cityRepository = cityRepository;
        this.currentWeatherRepository = currentWeatherRepository;
        this.forecastRepository = forecastRepository;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    @Override
    @Cacheable("current-weather")
    public CurrentWeatherResponse getWeather(String city){
        String url = UriComponentsBuilder.fromUriString(apiUrl)
            .path("/weather")
            .queryParam("q", city)
            .queryParam("appid", apiKey)
            .queryParam("units", "metric")
            .toUriString();

        
        try{
            OpenWeatherMapResponse response = restTemplate.getForObject(url, OpenWeatherMapResponse.class);

            if (response != null && response.getMain() != null && response.getSys() != null) {

                persistWeatherData(response);

                return mapToCurrentWeatherResponse(response);
            } else{
                throw new CityNotFoundException("Weather data not available for city: " + city);
            }
        }catch (HttpClientErrorException e){
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.error("error at current-weather on city: {}",city, e);
                throw new CityNotFoundException("City not found: " + city);
        }
        log.error("API Client Error for forecast on city: {}. Status: {}", city, e.getStatusCode(), e);
        throw new RuntimeException("API Client Error: " + e.getStatusCode());
    }   
        
    }

    @Override
    @Cacheable("weather-forecast")
    public ForecastResponse getForecast(String city) {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .path("/forecast")
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();

        try {
            OpenWeatherMapForecastResponse response = restTemplate.getForObject(url, OpenWeatherMapForecastResponse.class);

            if (response == null || response.getList() == null || response.getList().isEmpty()) {
                log.warn("Forecast data not available or empty for city: {}", city);
                throw new CityNotFoundException("Forecast data not available for city: " + city);
            }
            log.debug("Received {} 3-hourly forecast items from API for city: {}", response.getList().size(), city);

            List<DailyForecast> dailyForecasts = aggregateForecastData(response.getList());

            persistForecastData(response, dailyForecasts);

            return mapToForecastResponse(response.getCityInfo(), dailyForecasts);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.error("API Client Error for forecast on city: {}. Status: {}", city, e.getStatusCode(), e);
                throw new CityNotFoundException("City not found for forecast: " + city);
            }
            throw new RuntimeException("API Client Error for forecast: " + e.getStatusCode());
        }
    }

    private String formatTimestamp(OpenWeatherMapResponse response, long dateTime){
        try{
            Instant instant = Instant.ofEpochSecond(dateTime);
            ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(response.getTimezone());
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneOffset);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            String formattedTimestamp = localDateTime.format(formatter);
            return formattedTimestamp;

        }catch (Exception e){
            System.err.println("Could not convert time object" + e.getMessage());
            return null;
        }
    }

    private void persistWeatherData(OpenWeatherMapResponse response) {

        City cityEntity = cityRepository.findByName(response.getCity());
        if (cityEntity == null) {
            cityEntity = new City();
            cityEntity.setName(response.getCity());
            cityEntity.setCountry(response.getSys().getCountry());
            cityEntity.setLatitude(response.getCoord().getLat());
            cityEntity.setLongitude(response.getCoord().getLon());
        }

        cityEntity.setLastSearched(LocalDateTime.now());
        cityEntity.setSearchCount(cityEntity.getSearchCount() + 1);

        CurrentWeather currentWeather = cityEntity.getCurrentWeather();
        if (currentWeather == null) {
            currentWeather = new CurrentWeather();
            currentWeather.setCity(cityEntity);
            cityEntity.setCurrentWeather(currentWeather);
        }
        String sunrise= formatTimestamp(response, response.getSys().getSunrise());
        String sunset= formatTimestamp(response, response.getSys().getSunset());

        currentWeather.setTemperature(response.getMain().getTemperature());
        currentWeather.setHumidity(response.getMain().getHumidity());
        currentWeather.setWindSpeed(response.getWind().getWindSpeed());
        currentWeather.setWindDirection(response.getWind().getWindDirection());
        currentWeather.setPressure(response.getMain().getPressure());
        currentWeather.setWeatherMain(response.getWeather().get(0).getConditions());
        currentWeather.setWeatherDesc(response.getWeather().get(0).getDescription());
        currentWeather.setSunrise(sunrise);
        currentWeather.setSunset(sunset);
        currentWeather.setLastUpdated(LocalDateTime.now());

        cityRepository.save(cityEntity);
}

    private CurrentWeatherResponse mapToCurrentWeatherResponse(OpenWeatherMapResponse response) {

        String formattedDateTimestamp = formatTimestamp(response, response.getDt());
        String sunrise = formatTimestamp(response, response.getSys().getSunrise());
        String sunset = formatTimestamp(response, response.getSys().getSunset());
        String description = null;
        String conditions = null;

        if (response.getWeather() != null && !response.getWeather().isEmpty()) {
            description = response.getWeather().get(0).getDescription();
            conditions = response.getWeather().get(0).getConditions();
        }
        String weatherIcon = mapWeatherConditionToIconClass(conditions);
        
        return new CurrentWeatherResponse(response.getCity(), response.getMain().getTemperature(),
                response.getSys().getCountry(),formattedDateTimestamp,sunrise,sunset,response.getMain().getHumidity(),
                response.getMain().getPressure(),response.getWind().getWindSpeed(),response.getWind().getWindDirection(),
                conditions,description,weatherIcon);
    }

    private List<DailyForecast> aggregateForecastData(List<OpenWeatherMapForecastResponse.ForecastItem> forecastItems) {

        Map<LocalDate, List<OpenWeatherMapForecastResponse.ForecastItem>> itemsByDay = forecastItems.stream()
                .collect(Collectors.groupingBy(
                        item -> LocalDateTime.parse(item.getDateTimeText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        
        log.debug("Grouped forecast items into {} days.", itemsByDay.size());

        List<DailyForecast> dailyForecasts = new ArrayList<>();
        for (Map.Entry<LocalDate, List<OpenWeatherMapForecastResponse.ForecastItem>> entry : itemsByDay.entrySet()) {
            LocalDate date = entry.getKey();
            List<OpenWeatherMapForecastResponse.ForecastItem> itemsForDay = entry.getValue();

            double rainMillimeter = 0;

            double minTemp = itemsForDay.stream()
                    .mapToDouble(item -> item.getMain().getTemperature())
                    .min().orElse(0.0);

            double maxTemp = itemsForDay.stream()
                    .mapToDouble(item -> item.getMain().getTemperature())
                    .max().orElse(0.0);

            String description = itemsForDay.get(0).getWeather().get(0).getDescription();

            String conditions = itemsForDay.get(0).getWeather().get(0).getConditions();

            double humidity = itemsForDay.get(0).getMain().getHumidity();

            double windSpeed = itemsForDay.get(0).getWind().getWindSpeed();

            int pressure = itemsForDay.get(0).getMain().getPressure();

            float windDirection = itemsForDay.get(0).getWind().getWindDirection();

            float rainProbability = itemsForDay.get(0).getPop();

            String weatherIcon = mapWeatherConditionToIconClass(conditions);

            if (itemsForDay.get(0).getRain() != null && itemsForDay.get(0).getRain().getRainMm() != null){
                rainMillimeter = itemsForDay.get(0).getRain().getRainMm();
            }

            

            dailyForecasts.add(new DailyForecast(date.toString(), maxTemp, minTemp, description,conditions,humidity,pressure,windSpeed,windDirection,rainProbability,rainMillimeter,weatherIcon));
        }
        log.info("Successfully aggregated into {} daily forecasts.", dailyForecasts.size());
        return dailyForecasts;
    }
    private ForecastResponse mapToForecastResponse(OpenWeatherMapForecastResponse.CityInfo cityInfo, List<DailyForecast> dailyForecasts) {
        return new ForecastResponse(
                cityInfo.getName(),
                cityInfo.getCountry(),
                dailyForecasts
        );
    }

    private void persistForecastData(OpenWeatherMapForecastResponse apiResponse, List<DailyForecast> dailyForecasts) {

        City cityEntity = cityRepository.findByName(apiResponse.getCityInfo().getName());
        if (cityEntity == null) {
            cityEntity = new City();
            cityEntity.setName(apiResponse.getCityInfo().getName());
            cityEntity.setCountry(apiResponse.getCityInfo().getCountry());
            cityEntity.setLatitude(apiResponse.getCityInfo().getCoord().getLat());
            cityEntity.setLongitude(apiResponse.getCityInfo().getCoord().getLon());
        }

        final City finalCityEntity = cityEntity;

        for (DailyForecast dailyDto : dailyForecasts) {
            LocalDate forecastDate = LocalDate.parse(dailyDto.getDate());

            Forecast forecastEntity = cityEntity.findForecastForDate(forecastDate)
                .orElseGet(() -> {
                    Forecast newForecast = new Forecast();
                    newForecast.setCity(finalCityEntity);
                    finalCityEntity.getForecasts().add(newForecast);
                    return newForecast;
                });

            forecastEntity.setForecastDate(forecastDate);
            forecastEntity.setTemperature(dailyDto.getMaxTemperature());
            forecastEntity.setWeatherDesc(dailyDto.getDescription());
            forecastEntity.setWeatherMain(dailyDto.getConditions());
            forecastEntity.setWindSpeed(dailyDto.getWindSpeed());
            forecastEntity.setWindDirection(dailyDto.getWindDirection());
            forecastEntity.setHumidity(dailyDto.getHumidity());
            forecastEntity.setPressure(dailyDto.getPressure());
            forecastEntity.setProbability(dailyDto.getRainProbability());
            forecastEntity.setRainVolume(dailyDto.getRainMillimeter());    
    }

        cityRepository.save(cityEntity);
    }

    private String mapWeatherConditionToIconClass(String condition) {
        if (condition == null) {
            return "bi-sun-fill";
        }

        return switch (condition.toLowerCase()) {
            case "clear" -> "bi-sun-fill";
            case "clouds" -> "bi-cloud-fill";
            case "rain", "drizzle" -> "bi-cloud-rain-fill";
            case "thunderstorm" -> "bi-cloud-lightning-rain-fill";
            case "snow" -> "bi-cloud-snow-fill";
            case "mist", "smoke", "haze", "dust", "fog", "sand", "ash", "squall", "tornado" -> "bi-wind";
            default -> "bi-sun-fill";
        };
    }
        
}
