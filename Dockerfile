# Use an official Java runtime as a parent image
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml file
COPY pom.xml .

# Copy source code
COPY src ./src

RUN mvn clean verify

FROM openjdk:21-jdk AS runner

# Copy the Maven-built jar file into the container
COPY --from=builder ./app/target/CardCostApi-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]