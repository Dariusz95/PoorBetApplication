# FROM maven:3.9.8-eclipse-temurin-24 AS build
FROM maven:3.9.9-eclipse-temurin-21-jammy AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# FROM amazoncorretto:21.0.6-alpine3.20
FROM amazoncorretto:21-alpine3.20
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]