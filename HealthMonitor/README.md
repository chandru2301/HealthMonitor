# Health and Fitness Monitor

A comprehensive health and fitness monitoring system built with Spring Boot, Java, and following Object-Oriented Programming (OOP) principles. This application tracks steps, calculates BMR (Basal Metabolic Rate), monitors calories, and manages user activities.

## Features

- **User Management**: Create, read, update, and delete users with health profile information
- **Health Metrics Tracking**: Track daily health metrics including:
  - Steps walked
  - Calories consumed and burned
  - Distance traveled
  - Active minutes
  - Water intake
  - Sleep hours
  - Heart rate
- **Activity Tracking**: Record and manage various activities (running, walking, cycling, etc.)
- **BMR Calculation**: Calculate Basal Metabolic Rate using Mifflin-St Jeor Equation
- **TDEE Calculation**: Calculate Total Daily Energy Expenditure based on activity level
- **Calorie Calculator**: Automatically calculate calories burned for activities using MET values
- **Weekly Statistics**: View aggregated weekly health statistics and summaries

## OOP Concepts Implemented

### 1. **Encapsulation**
- Private fields with public getters/setters in all model classes
- Service classes encapsulate business logic
- Data Transfer Objects (DTOs) for API communication

### 2. **Inheritance**
- `BaseEntity` abstract class provides common fields (id, timestamps) for all entities
- All entity classes (`User`, `HealthMetrics`, `Activity`) extend `BaseEntity`

### 3. **Polymorphism**
- `BMRAnalyzer` interface with `MifflinStJeorBMRAnalyzer` implementation
- `CalorieCalculator` interface with `StandardCalorieCalculator` implementation
- Easy to add new analyzer/calculator implementations

### 4. **Abstraction**
- Interfaces define contracts without implementation details
- Abstract classes provide common functionality
- Service layer abstracts data access complexity

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** for database operations
- **H2 Database** (in-memory for development)
- **Spring Validation** for data validation
- **Springdoc OpenAPI (Swagger)** for API documentation
- **Maven** for dependency management

## Project Structure

```
src/main/java/com/healthmonitor/
├── model/              # Entity classes (User, HealthMetrics, Activity, BaseEntity)
├── dto/                # Data Transfer Objects
├── repository/         # JPA Repository interfaces
├── service/            # Business logic layer
│   ├── calculator/     # Calculator interfaces
│   └── impl/          # Calculator implementations
├── controller/         # REST Controllers
├── exception/          # Exception handling
└── HealthMonitorApplication.java
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone or download the project**

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**:
   - API Base URL: `http://localhost:8080/api`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - H2 Console: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:healthmonitor`
     - Username: `sa`
     - Password: (leave empty)

## API Endpoints

### User Management
- `POST /api/users` - Create a new user
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/users/{id}/bmr` - Calculate BMR for user
- `GET /api/users/{id}/tdee` - Calculate TDEE for user

### Health Metrics
- `POST /api/users/{userId}/metrics` - Create/update health metrics
- `GET /api/users/{userId}/metrics/date/{date}` - Get metrics by date
- `GET /api/users/{userId}/metrics/range?startDate=...&endDate=...` - Get metrics by date range
- `POST /api/users/{userId}/metrics/steps?steps=...&date=...` - Add steps
- `GET /api/users/{userId}/metrics/today` - Get today's metrics

### Activities
- `POST /api/users/{userId}/activities` - Create activity
- `GET /api/users/{userId}/activities` - Get all activities
- `GET /api/users/{userId}/activities/{activityId}` - Get activity by ID
- `GET /api/users/{userId}/activities/range?startDate=...&endDate=...` - Get activities by date range
- `PUT /api/users/{userId}/activities/{activityId}` - Update activity
- `DELETE /api/users/{userId}/activities/{activityId}` - Delete activity

### Dashboard
- `GET /api/users/{userId}/dashboard/summary` - Get health summary
- `GET /api/users/{userId}/dashboard/weekly?weekStartDate=...` - Get weekly statistics

## Example API Usage

### Create a User
```bash
POST /api/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "dateOfBirth": "1990-01-15",
  "gender": "MALE",
  "heightCm": 175.0,
  "weightKg": 75.0,
  "activityLevel": "MODERATELY_ACTIVE"
}
```

### Add Steps
```bash
POST /api/users/1/metrics/steps?steps=5000&date=2024-01-20
```

### Create an Activity
```bash
POST /api/users/1/activities
Content-Type: application/json

{
  "activityType": "RUNNING",
  "startTime": "2024-01-20T08:00:00",
  "endTime": "2024-01-20T08:30:00",
  "distanceKm": 5.0
}
```

## BMR Calculation

The application uses the **Mifflin-St Jeor Equation** for BMR calculation:

- **For Men**: BMR = (10 × weight) + (6.25 × height) - (5 × age) + 5
- **For Women**: BMR = (10 × weight) + (6.25 × height) - (5 × age) - 161

## Calorie Calculation

Calories burned are calculated using MET (Metabolic Equivalent) values:
- **Formula**: Calories = MET × weight(kg) × time(hours)

The system includes MET values for common activities:
- Walking: 3.5 MET
- Jogging: 7.0 MET
- Running: 9.8 MET
- Cycling: 4.0-10.0 MET (depending on intensity)
- Swimming: 6.0-10.0 MET
- And more...

## Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package
java -jar target/health-monitor-1.0.0.jar
```

## Future Enhancements

- User authentication and authorization
- Integration with fitness devices/wearables
- Mobile app support
- Advanced analytics and charts
- Goal setting and tracking
- Social features (friends, challenges)
- Export data to CSV/PDF
- Integration with external health APIs

## License

This project is created for educational purposes demonstrating Java OOP concepts and Spring Boot best practices.

