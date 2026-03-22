FROM maven:3.9.9-eclipse-temurin-21-jammy AS build

WORKDIR /workspace

COPY pom.xml ./
COPY poorbet-commons/pom.xml poorbet-commons/pom.xml
COPY match-service/pom.xml match-service/pom.xml
COPY poorbet-commons/src poorbet-commons/src
COPY match-service/src match-service/src

RUN mvn -pl match-service -am -DskipTests package -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/match-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
