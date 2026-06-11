# ============================================
# Stage 1: Build with Maven
# ============================================
FROM maven:3.6.3-jdk-8 AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first (cache dependencies)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw.cmd ./

# Download dependencies (cache layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the JAR
RUN mvn clean package -DskipTests -B -q

# ============================================
# Stage 2: Run with JRE
# ============================================
FROM openjdk:8-jre-slim

WORKDIR /app

# Create data directory for H2 database
RUN mkdir -p /app/data

# Copy the built JAR
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD java -cp app.jar org.springframework.boot.loader.JarLauncher --health-check || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
