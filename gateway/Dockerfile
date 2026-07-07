FROM maven:3.9.9-eclipse-temurin-21-jammy AS build
WORKDIR /workspace

COPY pom.xml ./
COPY gateway/pom.xml gateway/pom.xml
COPY gateway/src gateway/src

RUN --mount=type=secret,id=github_actor \
    --mount=type=secret,id=github_token \
    mkdir -p /root/.m2 && \
    printf '<settings><servers><server><id>github-commons</id><username>%s</username><password>%s</password></server><server><id>github-auth</id><username>%s</username><password>%s</password></server></servers></settings>' \
      "$(cat /run/secrets/github_actor)" "$(cat /run/secrets/github_token)" \
      "$(cat /run/secrets/github_actor)" "$(cat /run/secrets/github_token)" \
      > /root/.m2/settings.xml && \
    mvn -f gateway/pom.xml -DskipTests package -q && \
    rm -f /root/.m2/settings.xml

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S -u 1001 -G appgroup appuser
COPY --from=build /workspace/gateway/target/*.jar app.jar
USER 1001
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
