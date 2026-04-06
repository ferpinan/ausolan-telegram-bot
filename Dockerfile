# =========================
# 1. Build stage (Maven)
# =========================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiamos primero solo el pom para cachear dependencias
COPY pom.xml .

RUN mvn -B -q -e -DskipTests dependency:go-offline

# Ahora copiamos el código
COPY src ./src

# Compilamos el jar
RUN mvn clean package -DskipTests

# =========================
# 2. Runtime stage (ligero)
# =========================
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copiamos el jar generado
COPY --from=build /app/target/*.jar app.jar

# Ejecutamos la app
ENTRYPOINT ["java", "-jar", "app.jar"]