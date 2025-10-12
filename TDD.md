
# Technical Design Document

## Weather Forecast Service

### 1. Architecture Overview

#### 1.1 High-level Architecture

The Weather Forecast Service will follow a layered architecture within a monolithic Spring Boot application:

```
┌─────────────────────────────────────────────────────────┐
│                   Weather Forecast Service              │
│                                                         │
│  ┌─────────────┐   ┌─────────────┐   ┌─────────────┐    │
│  │  Web Layer  │   │Service Layer│   │  Data Layer │    │
│  │(Controllers/│<─>│  (Services) │<─>│(Repos)      │    │
│  │   Views)    │   │             │   │             │    │
│  └─────────────┘   └─────────────┘   └─────────────┘    │
│          ▲                 ▲                ▲           │
│          │                 │                │           │
└──────────┼─────────────────┼────────────────┼───────────┘
           │                 │                │
           ▼                 │                ▼
┌─────────────────┐          │         ┌──────────────────┐
│    Web Browser  │          │         │    Database      │
└─────────────────┘          │         └──────────────────┘
                             ▼
                    ┌──────────────────┐
                    │ OpenWeatherMap   │
                    │ API              │
                    └──────────────────┘
```

#### 1.2 Design Principles

- **Separation of Concerns**: Clear separation between UI, business logic, and data access
- **Single Responsibility**: Each component has a well-defined role
- **DRY (Don't Repeat Yourself)**: Reusable components and utilities
- **Fail Gracefully**: Handle errors and API failures appropriately

### 2. Technology Stack

#### 2.1 Backend

- **Programming Language**: Java 17
- **Web Framework**: Spring Boot 3.x
- **ORM**: Spring Data JPA with Hibernate
- **Database**: PostgreSQL
- **API Client**: Spring RestTemplate or WebClient
- **Caching**: Spring Cache with Caffeine
- **Scheduled Tasks**: Spring Scheduler
- **Testing**: JUnit 5, Mockito
- **Logging**: SLF4J with Logback
- **Build Tool**: Maven

#### 2.2 Frontend

- **View Engine**: Thymeleaf for server-side rendering
- **CSS Framework**: Bootstrap  or Tailwind CSS for styling
- **JavaScript**: Minimal vanilla JavaScript for interactivity

#### 2.3 Development Tools

- **IDE**: IntelliJ IDEA or Eclipse
- **Version Control**: Git
- **API Documentation**: Springdoc OpenAPI (Swagger)
- **Database Migration**: Flyway

### 3. Data Model

#### 3.1 Entity Relationship Diagram

```
┌────────────────┐     ┌────────────────┐     ┌────────────────┐
│     City       │     │ CurrentWeather │     │   Forecast     │
├────────────────┤     ├────────────────┤     ├────────────────┤
│ id             │     │ id             │     │ id             │
│ name           │     │ cityId         │     │ cityId         │
│ country        │     │ timestamp      │     │ forecastDate   │
│ latitude       │     │ temperature    │     │ temperature    │
│ longitude      │     │ humidity       │     │ windSpeed      │
│ lastSearched   │     │ windSpeed      │     │ windDirection  │
│ searchCount    │     │ windDirection  │     │ pressure       │
└────────────────┘     │ pressure       │     │ humidity       │
                       │ weatherMain    │     │ weatherMain    │
                       │ weatherDesc    │     │ weatherDesc    │
                       │ sunrise        │     │ rainVolume     │
                       │ sunset         │     │ probability    │
                       │ lastUpdated    │     └────────────────┘
                       └────────────────┘
```

#### 3.2 Detailed Entity Descriptions

**City**

- Primary entity representing a geographic location
- Tracks search frequency to prioritize data refresh
- Contains geographic coordinates for API calls

**CurrentWeather**

- Represents current weather conditions for a city
- One-to-one relationship with City
- Updated periodically by the scheduler

**Forecast**

- Represents future weather forecasts
- One-to-many relationship with City (multiple forecast points)
- Contains weather prediction for a specific point in time

### 4. API Design

#### 4.1 Third-party API Integration

The application will use the OpenWeatherMap API with the following endpoints:

- Current Weather: `https://api.openweathermap.org/data/2.5/weather`
- Forecast: `https://api.openweathermap.org/data/2.5/forecast`

Sample API request:

```
GET https://api.openweathermap.org/data/2.5/weather?q=Berlin&appid={apiKey}&units=metric
```

#### 5.2 REST API Endpoints

**Current Weather**

```
GET /api/weather/current?city={cityName}
```

Response:

```json
{
  "city": "Berlin",
  "country": "DE",
  "timestamp": "2025-03-21T14:30:00",
  "temperature": 12.5,
  "humidity": 65,
  "windSpeed": 5.2,
  "windDirection": 180,
  "pressure": 1012,
  "conditions": "Partly Cloudy",
  "description": "scattered clouds",
  "sunrise": "2025-03-21T06:12:00",
  "sunset": "2025-03-21T18:34:00"
}
```

**Forecast**

```
GET /api/weather/forecast?city={cityName}
```

Response:

```json
{
  "city": "Berlin",
  "country": "DE",
  "forecasts": [
    {
      "date": "2025-03-22T12:00:00",
      "temperature": 14.2,
      "humidity": 60,
      "windSpeed": 4.8,
      "conditions": "Sunny",
      "description": "clear sky"
    },
    {
      "date": "2025-03-23T12:00:00",
      "temperature": 15.7,
      "humidity": 58,
      "windSpeed": 3.9,
      "conditions": "Partly Cloudy",
      "description": "few clouds"
    },
    {
      "date": "2025-03-24T12:00:00",
      "temperature": 13.1,
      "humidity": 72,
      "windSpeed": 6.2,
      "conditions": "Rain",
      "description": "light rain"
    }
  ]
}
```

### 5. Key Implementation Details

#### 5.1 Caching Strategy

- Use Spring Cache with Caffeine provider
- Cache configurations:
  - Current weather: 15 minutes TTL
  - Forecast data: 1 hour TTL
  - City search results: 24 hours TTL

#### 5.2 Scheduled Tasks

- Implement scheduled tasks to refresh weather data
- Focus on frequently searched cities
- Configure with sensible intervals to avoid API rate limits

#### 5.3 Error Handling

- Implement global exception handler
- Provide graceful degradation when API is unavailable
- Return cached data when possible

#### 5.4 View Templates

Key Thymeleaf templates:

- `home.html`: Search form and recent searches
- `weather.html`: Current weather and forecast display

### 6. Testing Strategy

#### 6.1 Unit Testing

- Test individual components in isolation
- Mock external dependencies
- Focus on service and utility classes

#### 6.2 Integration Testing

- Test interaction between components
- Test database operations
- Mock external API calls

#### 6.3 API Testing

- Test REST API endpoints
- Verify response formats
- Test error handling
