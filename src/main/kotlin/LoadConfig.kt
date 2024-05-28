package org.example

import io.netty.handler.codec.mqtt.MqttQoS

data class LoadConfig(
    val qos : MqttQoS = MqttQoS.AT_MOST_ONCE,
    val keepAliveSec : Int = 15,
    val messagePayloadSize: Int? = null,
    val nMessagesPerChannel: Int = 100,
    val channelsPerGroup : Int = 5,
    val eventLoopsPerGroup : Int = 10,
    val amountOfGroups : Int = 1,
    val messagePayloadBytes : ByteArray? = ByteArray(0),
) {

}