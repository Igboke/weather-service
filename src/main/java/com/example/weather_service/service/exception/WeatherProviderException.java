package com.example.weather_service.service.exception;

public class WeatherProviderException extends RuntimeException{
        
    public WeatherProviderException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
