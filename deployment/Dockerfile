# syntax=docker/dockerfile:1

# ---------- Stage 1: build the bootJar ----------
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace

# Copy the Gradle wrapper and build scripts first to leverage layer caching.
COPY gradlew gradlew.bat settings.gradle build.gradle main.gradle gradle.properties lombok.config ./
COPY gradle ./gradle

# Copy the module sources.
COPY domain ./domain
COPY infrastructure ./infrastructure
COPY applications ./applications

# Build the executable bootJar (skip tests here; they run in CI).
RUN chmod +x ./gradlew \
    && ./gradlew :app-service:bootJar -x test --no-daemon

# ---------- Stage 2: minimal runtime image ----------
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Run as an unprivileged user.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# The bootJar is named after the root project: franchise-api.jar
# (see :app-service bootJar archiveFileName, which uses the parent project name).
COPY --from=build /workspace/applications/app-service/build/libs/franchise-api.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["/bin/sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
