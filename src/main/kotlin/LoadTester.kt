package org.example

import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.mqtt.MqttQoS
import org.example.mqtt.MqttClientInitializer
import java.net.InetSocketAddress
import java.util.concurrent.Executors

class LoadTester(
    private val broker : String,
    private val port : Int,
    private val topic : String,
    private val loadConfig: LoadConfig
){


    fun launch(useMetrics : Boolean = false){

        val startTime = System.currentTimeMillis()
        val executor = Executors.newFixedThreadPool(loadConfig.amountOfGroups)

        try {
            var infoMetrics : MessageInfoMetrics? =  null
            if(useMetrics){
                infoMetrics = MessageInfoMetrics(loadConfig)
            }
            repeat(loadConfig.amountOfGroups) {groupId->
                executor.submit {
                    val eventLoopGroup = NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2)
                    try {
                        val bootstrap = Bootstrap()
                        bootstrap.group(eventLoopGroup)
                            .channel(NioSocketChannel::class.java)
                            .handler(MqttClientInitializer(groupId,topic, loadConfig,infoMetrics))
                        for(i in 1..loadConfig.channelsPerThread){
                            /*println("channel ${i}")*/
                            val future = bootstrap.connect(InetSocketAddress(broker, port)).sync()
                            future.channel().closeFuture().sync()
                        }



                    } finally {
                        val endTime = System.currentTimeMillis()
                        val duration = endTime - startTime

                        println("Execution time: ${groupId} $duration ms")
                        infoMetrics?.let {
                            println("Sent : ${infoMetrics.getSentMessageAmount()}")
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