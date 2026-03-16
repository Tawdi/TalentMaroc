# Phase 1: Build (Maven + JDK + Cache deps)
FROM maven:3.8.4-openjdk-17-slim AS builder
WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Phase 2: Runtime (JRE Alpine)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8093

# Container-aware JVM options
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=25.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
