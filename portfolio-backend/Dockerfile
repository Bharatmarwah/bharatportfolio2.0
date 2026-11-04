# syntax=docker/dockerfile:1

# ---- build stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Dependencies layer
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline

# Application source
COPY src ./src
RUN mvn -B -Dmaven.test.skip=true package

# ---- runtime stage ----
FROM eclipse-temurin:21-jre-jammy

RUN groupadd --system app && useradd --system --gid app --home-dir /app app

WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
RUN chown -R app:app /app

USER app
EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseContainerSupport"

# Honour PORT (Render/Railway/Fly), then SERVER_PORT, then 8080.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar --server.port=${PORT:-${SERVER_PORT:-8080}}"]
