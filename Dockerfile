FROM gradle:8.6.0-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM amazoncorretto:21-alpine3.18
EXPOSE 8080:8080
ENV BROKER_IP="localhost"
ENV BROKER_PORT=1883
ENV BROKER_TOPIC="test"
ENV MQTT_USERNAME=""
ENV MQTT_PASSWORD=""
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/ktor-loadtester.jar
ENTRYPOINT ["java","-jar","/app/ktor-loadtester.jar"]