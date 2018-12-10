FROM adoptopenjdk/openjdk11:alpine

WORKDIR /deployments

# Application
COPY build/vertx-twitter.jar .

EXPOSE 8080

CMD ["java", "-jar", "vertx-twitter.jar"]

