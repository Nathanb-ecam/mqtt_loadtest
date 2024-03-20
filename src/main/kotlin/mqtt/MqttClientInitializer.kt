package org.example.mqtt

import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.handler.codec.mqtt.MqttDecoder
import io.netty.handler.codec.mqtt.MqttEncoder
import io.netty.handler.codec.mqtt.MqttQoS
import org.example.LoadConfig
import org.example.MessageInfoMetrics

class MqttClientInitializer(
    private val groupId : Int,
    private val topic: String,
    private val loadConfig: LoadConfig,
    private val messageInfoMetrics: MessageInfoMetrics?
) : ChannelInitializer<Channel>() {
    private var channelId = 0

    override fun initChannel(ch: Channel) {

        val pipeline = ch.pipeline()
        pipeline.addLast(MqttEncoder.INSTANCE)
        pipeline.addLast(MqttDecoder())
        pipeline.addLast(MqttClientHandler(groupId,channelId,topic, loadConfig,messageInfoMetrics))
        channelId++
    }
}