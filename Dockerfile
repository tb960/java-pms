# Multi-stage build - Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# Set working directory in container
WORKDIR /build

# Copy pom.xml
COPY pom.xml .

# Download dependencies and verify build
# Using -q for quiet mode, -B for batch mode, -DskipTests to skip tests during dependency download
RUN mvn clean install -q -B -DskipTests -Dmaven.test.skip=true || \
    mvn clean install -B -DskipTests -Dmaven.test.skip=true

# Copy the entire source code
COPY src ./src

# Build the application
RUN mvn clean package -q -B -DskipTests -Dmaven.test.skip=true

# ===============================================
# Stage 2: Runtime - Minimal image with just the JAR
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy only the compiled JAR from builder stage
COPY --from=builder /build/target/optical-shop-erp-1.0.0.jar app.jar

# Create a non-root user for security
RUN useradd -m -u 1000 appuser && chown -R appuser:appuser /app
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/health || exit 1

# Run with dev profile by default
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--spring.profiles.active=dev"]
