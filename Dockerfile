# Step 1: Use a base image with JDK (OpenJDK is a pop   ular choice)
FROM openjdk:17-jdk-slim as builder

# Step 2: Set the working directory in the container
WORKDIR /app

# Step 3: Copy the JAR file from your local machine into the Docker container
# Assume your JAR file is in target/ or build/libs/
COPY target/springbootwebsocketangular-0.0.1-SNAPSHOT.jar app.jar

# Step 4: Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Optional: Expose the port your Spring Boot app runs on (default is 8080)
EXPOSE 8080