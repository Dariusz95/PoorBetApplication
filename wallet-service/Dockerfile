FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S -u 1001 -G appgroup appuser

COPY target/*.jar app.jar

USER 1001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
