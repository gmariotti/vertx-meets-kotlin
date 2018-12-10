FROM adoptopenjdk/openjdk11:alpine

WORKDIR /deployments

# Application
COPY build/vertx-reactive.jar .

EXPOSE 8080

CMD ["java", "-jar", "vertx-reactive.jar", "api.yml"]

