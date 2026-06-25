FROM maven:3.9.9-eclipse-temurin-21-jammy AS build

WORKDIR /workspace

COPY pom.xml ./
COPY poorbet-commons/pom.xml poorbet-commons/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY poorbet-commons/src poorbet-commons/src
COPY auth-service/src auth-service/src

RUN mvn -pl auth-service -am -DskipTests package -q

FROM amazoncorretto:21-alpine3.20
WORKDIR /app
RUN addgroup -S appgroup && adduser -S -u 1001 -G appgroup appuser
COPY --from=build /workspace/auth-service/target/*.jar app.jar
USER 1001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
