# =================================================
# Build Stage: Compilation and Packaging
# =================================================

# Use the official Maven image with Eclipse Temurin JDK 21 for the build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project object model (POM) file
COPY pom.xml .

# Copy the application source code
COPY src ./src

# Build the project and package it into a JAR file, skipping tests
RUN mvn clean package -DskipTests


# =================================================
# Runtime Stage: Application Deployment
# =================================================

# Use the official OpenJDK slim image for the runtime stage
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8443 for HTTPS communication
EXPOSE 8443

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]