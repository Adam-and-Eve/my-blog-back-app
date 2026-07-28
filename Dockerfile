FROM gradle:9.6.1-jdk21 AS builder
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./

COPY src ./src

RUN ./gradlew war -x test --no-configuration-cache

FROM tomcat:11.0-jdk21-temurin-noble
WORKDIR /usr/local/tomcat

RUN rm -rf webapps/*

COPY --from=builder /app/build/libs/*.war webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]