# ── STAGE 1 : BUILD ──────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app


COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN ./mvnw dependency:go-offline -B || true

# Copier le code source ensuite
COPY src src

RUN ./mvnw clean package -DskipTests

# ── STAGE 2 : RUNTIME ───────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

