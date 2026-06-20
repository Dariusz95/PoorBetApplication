FROM maven:3.9.9-eclipse-temurin-21-jammy AS build
WORKDIR /app

COPY ../pom.xml ./pom.xml
COPY ../poorbet-commons ./poorbet-commons
COPY ../gateway ./gateway

RUN mvn clean install -DskipTests -f poorbet-commons/pom.xml

RUN mvn clean package -DskipTests -f gateway/pom.xml

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S -u 1001 -G appgroup appuser
COPY --from=build /app/gateway/target/*.jar app.jar
USER 1001
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]