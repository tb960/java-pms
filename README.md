# Optical Shop ERP

Enterprise Resource Planning System for Optical Shop Management

## Project Overview

This is a Spring Boot-based backend application designed to manage optical shop operations including:
- Product inventory management
- Sales and invoicing
- Customer management
- Stock control
- Reporting and analytics

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Java Version**: Java 17
- **Database**: MySQL (Development: H2)
- **Build Tool**: Maven
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security with JWT
- **Additional Libraries**:
  - Lombok (boilerplate reduction)
  - MapStruct (DTO mapping)
  - Spring Validation

## Prerequisites

### Option A: Docker (Recommended - No installation needed!)
- **Only requirement**: Docker Desktop
- Everything else (Java, Maven, MySQL) runs inside Docker
- Download: https://www.docker.com/products/docker-desktop

### Option B: Local Setup
- Java 17 or higher
- Maven 3.8.0 or higher
- MySQL 8.0 or higher (optional, use H2 for dev)
- Git

## Quick Start with Docker 🐳

```bash
cd /Users/bkblyde/Documents/proj/java-pms

# Build and start everything
make dev-up

# Test the application
make test

# View logs
make logs-tail

# Stop everything
make down
```

**That's it!** No need to install Java, Maven, or anything else on your Mac!

See [DOCKER.md](DOCKER.md) for detailed Docker documentation.

## Project Setup (Local Development)

### 1. Clone the Repository
```bash
git clone <repository-url>
cd java-pms
```

### 2. Install Maven (if not using Docker)
```bash
brew install maven
mvn --version
```

### 3. Database Configuration

#### For Development (H2 - Easiest):
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```
Access H2 console at: http://localhost:8080/api/h2-console

#### For Production (MySQL):
1. Create a database:
```sql
CREATE DATABASE optical_shop_erp;
```

2. Update `src/main/resources/application.yml` with your MySQL credentials

### 4. Build the Project
```bash
mvn clean install
```

### 5. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

## Project Structure

```
src/
├── main/
│   ├── java/com/opticalshop/erp/
│   │   ├── OpticalShopErpApplication.java  (Main entry point)
│   │   ├── controller/                      (REST endpoints)
│   │   ├── service/                         (Business logic)
│   │   ├── repository/                      (Database access)
│   │   ├── model/                           (JPA entities)
│   │   ├── dto/                             (Data transfer objects)
│   │   ├── exception/                       (Custom exceptions)
│   │   ├── config/                          (Configuration classes)
│   │   └── security/                        (Security configuration)
│   └── resources/
│       ├── application.yml                  (Main config)
│       └── application-dev.yml              (Dev profile)
└── test/
    └── java/com/opticalshop/erp/
```

## Available Endpoints

### Health Check
```
GET /api/health
```

### Products (To be implemented)
```
GET    /api/products           - Get all products
POST   /api/products           - Create new product
GET    /api/products/{id}      - Get product by ID
PUT    /api/products/{id}      - Update product
DELETE /api/products/{id}      - Delete product
```

## Development Guidelines

### Code Organization
- **Controllers**: Handle HTTP requests/responses
- **Services**: Contain business logic
- **Repositories**: Data access layer
- **Models**: Database entities
- **DTOs**: Data transfer objects for API communication

### Naming Conventions
- Controllers: `*Controller`
- Services: `*Service`
- Repositories: `*Repository`
- DTOs: `*DTO` or `*Request`/`*Response`

### Adding New Features
1. Create entity in `model/` package
2. Create repository in `repository/` package
3. Create service in `service/` package
4. Create controller in `controller/` package
5. Write unit tests in `src/test/`

## Configuration Files

### application.yml
Main configuration file with Spring settings, database connection, JPA properties, and logging configuration.

### application-dev.yml
Development profile using H2 in-memory database for quick testing.

## Maven Commands

```bash
# Build project
mvn clean install

# Run tests
mvn test

# Run application
mvn spring-boot:run

# Package as JAR
mvn clean package

# Create with specific profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

## Next Steps

1. Implement Product API endpoints
2. Create Customer entity and API
3. Implement Sales/Invoice functionality
4. Add authentication and authorization
5. Create unit and integration tests
6. Add API documentation (Swagger/OpenAPI)

## Contributing

1. Create a feature branch
2. Make your changes
3. Write tests
4. Submit a pull request

## License

See LICENSE file for details.

## Support

For issues and questions, please create an issue in the repository.
