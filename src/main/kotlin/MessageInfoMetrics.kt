package org.example

class MessageInfoMetrics(
    private var loadConfig: LoadConfig,



) {
    private var messageCount : Int = 0


    @Synchronized
    fun increment(){
        messageCount++
    }

    fun getSentMessageAmount(): Int{
        return messageCount
    }


    fun printParameters(){
        val theoreticalValue = loadConfig.amountOfGroups * loadConfig.channelsPerThread * loadConfig.nMessagesPerChannel
        println("Load parameters :")
        println("group(s) : ${loadConfig.amountOfGroups} ")
        println("thread(s) : ${loadConfig.eventLoopsPerGroup} ")
        println("channel(s)/thread : ${loadConfig.channelsPerThread}")
        println("messages/channel : ${loadConfig.nMessagesPerChannel} ")
        println("Keep alive : ${loadConfig.keepAliveSec}")
        println("qos : ${loadConfig.qos}")
        println("payload size : ${loadConfig.messagePayloadSize}")
        println("${theoreticalValue}")
    }
}