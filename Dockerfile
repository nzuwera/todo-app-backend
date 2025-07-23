FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ARG APP_VERSION
ENV APP_VERSION=${APP_VERSION}

COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]