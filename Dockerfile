FROM openjdk:17-jdk-slim as build

WORKDIR /app
COPY ./target/chat.jar chat.jar
COPY ./target/libs libs
ENTRYPOINT java -jar chat.jar
EXPOSE 8080