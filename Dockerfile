FROM gradle:8.6.0-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon


FROM openjdk:21-jdk-slim
EXPOSE 8080
ENV BROKER_IP="localhost"
ENV BROKER_PORT=1883
ENV BROKER_TOPIC="test"
ENV MQTT_USERNAME="iCureIoTUser"
ENV MQTT_PASSWORD="iCureIoTPassword"
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/ktor-loadtester.jar

CMD ["java", "-jar", "ktor-loadtester.jar"]


