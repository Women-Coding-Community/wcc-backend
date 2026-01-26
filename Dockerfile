# Stage 1: Build the application using Gradle and JDK 21 (Temurin)
FROM gradle:8.7-jdk21-alpine AS build
WORKDIR /app

# Copy configuration files to cache dependencies
COPY build.gradle.kts settings.gradle.kts ./

# Copy source code and build the application
# Using the 'gradle' command directly since it's pre-installed
COPY src ./src
RUN gradle clean bootJar --no-daemon

# Stage 2: Minimal runtime image using Eclipse Temurin 21 JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/build/libs/*.jar app.jar
COPY --from=build /app/src/main/resources /app/resources

ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
ENV SPRING_PROFILES_ACTIVE=docker

# Application configuration
EXPOSE 8080 5005
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar