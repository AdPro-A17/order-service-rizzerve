# Use OpenJDK 21 base image
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy build/libs jar file into the container
COPY build/libs/*.jar app.jar

# Expose port (adjust if needed)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]