# Product Requirements Document (PRD)

## Weather Forecast Service

### 1. Product Overview

The Weather Forecast Service is a monolithic Java application that retrieves weather forecast data from a third-party API, stores this information in a database, and makes it available through both a web-based dashboard and a REST API. The service does not require authentication.

### 2. User Stories

1. As a visitor, I want to view weather data for a specific city by entering its name.
2. As a visitor, I want to see current weather conditions and a basic forecast.
3. As a developer, I want to access the same weather data via a REST API without authentication.

### 3. Functional Requirements

#### 3.1 Weather Data

- Retrieve current weather conditions for specified cities
- Retrieve basic 1-day forecast
- Support for essential weather data points:
  - Temperature
  - Humidity
  - Wind speed
  - Weather conditions (sunny, cloudy, rainy, etc.)
  - Pressure
  - Sunrise and sunset times

#### 3.2 City Search

- Search cities by exact name
- Maintain a list of recently searched cities in the database

#### 3.3 Dashboard

- Simple form to enter city name
- Current conditions display
- Basic forecast table
- Recent searches list

#### 3.4 REST API

- Endpoints for current weather by city name
- Endpoints for forecast by city name
- No authentication required

### 4. Non-functional Requirements

#### 4.1 Performance

- Dashboard page load time < 3 seconds
- API response time < 1 second

#### 4.2 Data Freshness

- Weather data not older than 2 hours
- Background job to refresh data for frequently searched cities

#### 4.3 Reliability

- System must handle third-party API outages gracefully
- Return cached data when third-party API is unavailable

#### 4.4 Scalability

- System should be designed to handle increasing number of city searches over time
