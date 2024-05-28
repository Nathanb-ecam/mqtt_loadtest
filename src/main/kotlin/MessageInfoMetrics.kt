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
            appendLine("group(s) : ${loadConfig.amountOfGroups} ")
            appendLine("thread(s) : ${loadConfig.eventLoopsPerGroup} ")
            appendLine("channel(s)/thread : ${loadConfig.channelsPerGroup}")
            appendLine("messages/channel : ${loadConfig.nMessagesPerChannel} ")
            appendLine("Keep alive : ${loadConfig.keepAliveSec}")
            appendLine("qos : ${loadConfig.qos}")
            appendLine("messagePayload :${loadConfig.messagePayloadBytes}")
            appendLine("payload size : ${loadConfig.messagePayloadSize}")
            appendLine("theoretical value : ${theoreticalMessageCount}")
        }
    }
}