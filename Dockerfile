# Multi-stage Dockerfile for Spring Boot (Gradle)

# 1) Build stage
FROM gradle:8-jdk17 AS build
WORKDIR /home/gradle/project

# Leverage layer caching for dependencies
COPY gradlew gradlew.bat build.gradle settings.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew --version

# Copy sources and build boot jar (skip tests for speed)
COPY src ./src
RUN ./gradlew clean bootJar -x test

# 2) Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy boot jar
COPY --from=build /home/gradle/project/build/libs/*-SNAPSHOT.jar /app/app.jar

# Container internal port
EXPOSE 8080

# Optional JVM options at runtime
ENV JAVA_OPTS=""

# Start application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]


