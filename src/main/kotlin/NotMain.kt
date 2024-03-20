package org.example

import org.eclipse.paho.client.mqttv3.*

fun main() {

    val startTime = System.currentTimeMillis()

    val brokerUrl = "tcp://localhost:1883"
    val topic = "test"
    val qos = 0
    val messageCount = 100000 // Number of messages to send
    val messagePayload = "Hello, MQTT!"

    val clientId = "LoadTestClient"


    try {
        val client = MqttClient(brokerUrl, clientId, null)
        val connOpts = MqttConnectOptions()
        connOpts.isCleanSession = true
        connOpts.userName = "iCureIoTUser"
        connOpts.password = "iCureIoTPassword".toCharArray()
        client.connect(connOpts)

        // Publish messages
        repeat(messageCount) {
            val message = MqttMessage(messagePayload.toByteArray())
            message.qos = qos
            client.publish(topic, message)
            /*println("Message $it sent")*/
        }

        // Disconnect from the broker
        client.disconnect()
        client.close()
    } catch (e: MqttException) {
        e.printStackTrace()
    }
    val endTime = System.currentTimeMillis()
    val duration = endTime - startTime

    println("Execution time: $duration ms")
}
