# Phase 1: Build (Maven + JDK)
FROM maven:3.8.4-openjdk-17-slim AS builder
WORKDIR /app

# Cache dependencies (changes only on pom.xml update)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source & build
COPY src ./src
RUN mvn clean package -DskipTests

# Phase 2: Runtime (JRE Alpine ~150MB)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8761
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=25.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
