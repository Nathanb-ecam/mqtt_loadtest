package org.example

class MessageInfoMetrics(
    private var loadConfig: LoadConfig,

) {
    private var messageCount = 0
    private var theoreticalMessageCount = 0

    init {
        theoreticalMessageCount = loadConfig.amountOfGroups * loadConfig.channelsPerGroup * loadConfig.nMessagesPerChannel
    }
    @Synchronized
    fun increment(){
        messageCount++
    }

    fun getSentMessageAmount(): Int{
        return messageCount
    }

    fun getTheoreticalMessageCount(): Int{
        return theoreticalMessageCount
    }

    fun loadParameters(): String{

        return buildString {
            appendLine("Load parameters :")
            appendLine("\t - group(s) : ${loadConfig.amountOfGroups} ")
            appendLine("\t - thread(s) : ${loadConfig.eventLoopsPerGroup} ")
            appendLine("\t - channel(s)/thread : ${loadConfig.channelsPerGroup}")
            appendLine("\t - messages/channel : ${loadConfig.nMessagesPerChannel} ")
            appendLine("\t - Keep alive : ${loadConfig.keepAliveSec}")
            appendLine("\t - qos : ${loadConfig.qos}")
            appendLine("\t - messagePayload :${loadConfig.messagePayloadBytes?.toString(Charsets.UTF_8)}")
            appendLine("\t - payload size : ${loadConfig.messagePayloadSize}")
            appendLine("\t - theoretical value : ${theoreticalMessageCount}")
        }
    }
}