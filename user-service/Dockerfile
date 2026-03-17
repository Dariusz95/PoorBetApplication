FROM maven:3.9.9-eclipse-temurin-21-jammy AS build

WORKDIR /workspace

COPY pom.xml ./
COPY poorbet-commons/pom.xml poorbet-commons/pom.xml
COPY user-service/pom.xml user-service/pom.xml
COPY poorbet-commons/src poorbet-commons/src
COPY user-service/src user-service/src

RUN mvn -pl user-service -am -DskipTests package -q

FROM amazoncorretto:21-alpine3.20
WORKDIR /app
COPY --from=build /workspace/user-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
