# Product price API

This is a Spring Boot project created for an interview challenge. 
It provides an API to retrieve the price of a product valid on a given date, filtered by product ID and brand ID. 
The project follows **hexagonal architecture** to ensure separation of concerns and flexibility for future enhancements.
It uses an SQL in memory database (H2), as this was a requirement of the challenge.

## Quick start

To run the project locally, follow these steps:

1. **Clone the repository**:
   ```bash
   git clone git@github.com:fabriciodigennaro/challenge-product-service.git
   cd challenge-product-service
   ```

2. **Build the project using Gradle**:

   ```bash
   ./gradlew clean build
   ```

3. **Run the application**:

   ```bash
   ./gradlew bootRun
   ```  
   The application will start at http://localhost:8080.

## Swagger API documentation
The project includes Swagger for API documentation and manual testing. After starting the application, you can view the
Swagger UI at:

http://localhost:8080/swagger-ui/index.html

## Requirements
- Java 21
- Gradle (or use the included Gradle wrapper)

## Running tests
To execute the tests, run:

```bash
./gradlew test
```

For test coverage, the project uses **JaCoCo**. To generate the coverage report, run:

```bash
./gradlew jacocoTestReport
```
The coverage report can be found at build/jacoco/html/index.html

To execute the minimum coverage check, run:
```bash
./gradlew jacocoTestCoverageVerification
```

## Test types

### Unit tests
These tests focus on verifying the correctness of individual components, primarily testing the use case in this project.
They ensure the core business logic functions as expected, isolated from external dependencies like databases or
external services.

### Integration tests
Tests the interaction with external services, such as the database.

### Contract tests
Verifies that the API complies with the expected contract, ensuring consistent integration with other systems.

### Component tests
Triggers an end-to-end flow to test the product price retrieval functionality.

## Design decisions

### Hexagonal architecture
The project is designed following the principles of hexagonal architecture. The domain and application logic are
isolated from infrastructure concerns, making the system flexible for future changes.

### No Spring annotations in domain/application layers
Framework-specific annotations (like Spring’s) are avoided in the domain and application layers, as the framework is
considered part of the infrastructure.
This keeps the business logic decoupled from the framework.

### Controlled bean configuration
Instead of using Spring stereotype annotations (@Component, @Service, etc.), all beans (except controllers) are 
configured in the config package.
This gives more control over bean lifecycle and allows for easier changes in the future.

### Direct use case invocation
In the controller, the use case class is called directly without an interface. Since the use case logic is not expected
to change frequently, adding an interface would increase complexity without significant benefit.
In this challenge, simplicity was prioritized.

### Value classes for Product ID and Brand ID
The use of value classes (classes that serve the purpose of wrapping values) was applied in the case of product ID and
brand ID, as these have the same type (long) and were used as parameters in the use case.
The value classes ProductId and BrandId were introduced to avoid a bug caused by accidentally switching the parameters.
All other properties were implemented without value classes to avoid adding unnecessary complexity in this challenge.

### Sealed class for use case response
The use case returns a sealed class, providing explicit control over success and failure scenarios.
Currently, only one failure case is handled (product price not found), but this design allows easy expansion for additional
failure scenarios in the future.
This approach avoids using exceptions for expected outcomes and provides more semantic meaning than using Optional or null.