# Start with a lightweight OpenJDK image
FROM eclipse-temurin:17-jre-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
# (Make sure the name matches your actual JAR file, e.g., account-transaction-api-0.0.1-SNAPSHOT.jar)
COPY target/account-transaction-api-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on (e.g., 8080 for Spring Boot)
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
