FROM eclipse-temurin:21-jdk-alpine as build

WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src src
RUN chmod +x ./mvnw
RUN ./mvnw -DskipTests package -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S -u 1001 -G appgroup appuser
COPY --from=build /app/target/*.jar app.jar
USER 1001
ENTRYPOINT ["java","-jar","/app/app.jar"]