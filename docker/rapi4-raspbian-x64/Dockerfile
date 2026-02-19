
FROM arm64v8/eclipse-temurin:17.0.14_7-jre-ubi9-minimal

# Set the working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY ./*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
