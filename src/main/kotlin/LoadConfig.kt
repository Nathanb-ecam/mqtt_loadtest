package org.example

import io.netty.handler.codec.mqtt.MqttQoS

data class LoadConfig(
    val qos : MqttQoS = MqttQoS.AT_MOST_ONCE,
    val keepAliveSec : Int = 10,
    val messagePayloadSize: Int = 10,
    val nMessagesPerChannel: Int = 100,
    val channelsPerThread : Int = 5,
    val eventLoopsPerGroup : Int = 10,
    val amountOfGroups : Int = 1
) {

}