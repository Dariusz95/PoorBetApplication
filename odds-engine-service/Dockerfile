FROM maven:3.9.9-eclipse-temurin-21-jammy AS build
WORKDIR /workspace

COPY pom.xml ./
COPY odds-engine-service/pom.xml odds-engine-service/pom.xml
COPY odds-engine-service/src odds-engine-service/src

RUN --mount=type=secret,id=github_actor \
    --mount=type=secret,id=github_token \
    mkdir -p /root/.m2 && \
    printf '<settings><servers><server><id>github-commons</id><username>%s</username><password>%s</password></server><server><id>github-auth</id><username>%s</username><password>%s</password></server></servers></settings>' \
      "$(cat /run/secrets/github_actor)" "$(cat /run/secrets/github_token)" \
      "$(cat /run/secrets/github_actor)" "$(cat /run/secrets/github_token)" \
      > /root/.m2/settings.xml && \
    mvn -f odds-engine-service/pom.xml -DskipTests package -q && \
    rm -f /root/.m2/settings.xml

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S -u 1001 -G appgroup appuser
COPY --from=build /workspace/odds-engine-service/target/*.jar app.jar
RUN chown -R appuser:appgroup /app
USER 1001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
