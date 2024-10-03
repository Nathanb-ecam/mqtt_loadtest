# Netty MQTT loadtester

The loadtest app is a netty based mqtt client (implemented with kotlin ktor) for creating a configurable amount of connections and messages.

## Configuration

BROKER_IP, BROKER_PORT and mqtt credentials (MQTT_USERNAME & MQTT_PASSWORD) must be setup through env variables.

## Launch a loadtest

  ```shell
  curl -X POST \
  http://localhost:8080/launch \
  -d 'messagePayloadSize=500' \
  -d 'nMessagesPerChannel=1000' \
  -d 'channelsPerGroup=100' \
  -d 'amountOfGroups=10' \
  -d 'token='
  ```
* each channel correspond to an MQTT connection

