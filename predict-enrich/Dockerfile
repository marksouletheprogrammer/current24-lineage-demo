# Use a Gradle image to build the application with JDK 21
FROM gradle:8.10.0-jdk21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy only the necessary files for dependency download
COPY build.gradle settings.gradle ./

# Download dependencies without building the project
RUN gradle dependencies

# Copy the entire project into the container
COPY . .

# Build the project
RUN gradle build

# Use an OpenJDK 21 image for the runtime environment
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built application from the build stage
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
