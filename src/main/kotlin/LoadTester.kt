package org.example

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import org.example.mqtt.MqttClientInitializer
import org.example.org.example.MqttCredentials
import java.lang.System.currentTimeMillis
import java.net.InetSocketAddress
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors


class LoadTester(
    private val broker : String,
    private val port : Int,
    private val topic : String,
    private val mqttCredentials : MqttCredentials,
    private val loadConfig: LoadConfig
){


    fun launch(useMetrics : Boolean = false){


        val startTime = currentTimeMillis()
        val executor = Executors.newFixedThreadPool(loadConfig.amountOfGroups)

        try {
            var infoMetrics : MessageInfoMetrics? =  null
            if(useMetrics){
                infoMetrics = MessageInfoMetrics(loadConfig)
            }


            val dateTime = Instant.ofEpochMilli(startTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()

            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            println("\n\n-------------------------------${dateTime.format(formatter)}-------------------------------")

            val eventLoopGroup = NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2)
            repeat(loadConfig.amountOfGroups) {groupId->
                executor.submit {

                    try {

                        val bootstrap = Bootstrap()
                        bootstrap.group(eventLoopGroup)
                            .channel(NioSocketChannel::class.java)
                            .handler(MqttClientInitializer(groupId,topic, loadConfig,mqttCredentials,infoMetrics))
                        for(i in 1..loadConfig.channelsPerGroup){
                            //println("channel ${i}")
                            val future = bootstrap.connect(InetSocketAddress(broker, port)).sync()
                            future.channel().closeFuture().sync()
                        }



                    } finally {
                        val endTime = currentTimeMillis()
                        val duration = endTime - startTime

                        println("Execution time group-${groupId} : $duration ms")
                        infoMetrics?.let {
                            println("Sent: ${infoMetrics.getSentMessageAmount()}")
                        }
                        eventLoopGroup.shutdownGracefully()
                    }
                }
            }

        }catch(e: Exception){
            e.printStackTrace()
        }
        finally {

            executor.shutdown()
        }
    }

}