# Use OpenJDK 21 base image
FROM eclipse-temurin:21-jdk # JDK, larger than JRE

# Set working directory
WORKDIR /app

# Copy build/libs jar file into the container
# ASSUMES THE JAR IS ALREADY BUILT ON THE HOST MACHINE
COPY build/libs/*.jar app.jar

# Expose port (adjust if needed)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]