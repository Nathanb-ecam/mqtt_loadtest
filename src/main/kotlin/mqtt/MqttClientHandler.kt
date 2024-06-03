package org.example.mqtt

import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.mqtt.*
import org.example.LoadConfig
import org.example.MessageInfoMetrics
import org.example.org.example.MqttCredentials

class MqttClientHandler(
    private val groupId: Int,
    private val channelId: Int,
    private val topic: String,
    private val loadConfig: LoadConfig,
    private val mqttCredentials: MqttCredentials,
    private val messageCounter: MessageInfoMetrics?
) : ChannelInboundHandlerAdapter() {

    //'{"cid":"daz","uid":"daz","token":"data":loadConfig.encryptedSelf}'


    private val payload = mapOf(
        "cid" to loadConfig.payload?.cid,
        "uid" to "g${groupId}-c${channelId}@loadtest.com",
        "token" to loadConfig.payload?.token,
        "data" to loadConfig.messagePayloadString
    )

    private val objectMapper = ObjectMapper()
    private var connectMessage: MqttMessage? = null
    private var messageSentCount = 0

    private val payloadBytes = if (loadConfig.messagePayloadString.isNotEmpty()) {
        objectMapper.writeValueAsBytes(payload)

    } else {
        ByteArray(loadConfig.messagePayloadSize!!)
    }

    private val message = MqttMessageFactory.newMessage(
        MqttFixedHeader(MqttMessageType.PUBLISH, false, loadConfig.qos, false, 0),
        MqttPublishVariableHeader(topic, 0),
        /*Unpooled.buffer().writeBytes(messagePayload.toByteArray())*/
        if(loadConfig.messagePayloadString.isNotEmpty()){
            Unpooled.buffer().writeBytes(payloadBytes)
        }else{
            Unpooled.buffer().writeBytes(ByteArray(loadConfig.messagePayloadSize!!))
        }

    )

    /*private var pubAckCount = 0*/

    init {
        connectMessage = MqttMessageFactory.newMessage(
            MqttFixedHeader(MqttMessageType.CONNECT, false, loadConfig.qos, false, 0),
            MqttConnectVariableHeader("MQTT", 4, true, true, false, 0, false, false, loadConfig.keepAliveSec),
            MqttConnectPayload(
                "group-${groupId}-channel-${channelId}",
                "",
                "",
                mqttCredentials.clientName,
                mqttCredentials.clientPassword
            )
        )
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.writeAndFlush(connectMessage)
    }



    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is MqttMessage) {
            if (msg.fixedHeader().messageType() == MqttMessageType.CONNACK) {
                //println("current user : ${payload["uid"]}")
                for (i in 1..loadConfig.nMessagesPerChannel) {




                    messageSentCount++
                    messageCounter?.increment()
                    ctx.writeAndFlush(message)
                }
                ctx.close()
            }
        }
    }



    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}