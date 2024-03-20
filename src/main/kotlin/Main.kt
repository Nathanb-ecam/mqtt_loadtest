package org.example


import io.netty.handler.codec.mqtt.*



fun main() {

    val broker = System.getenv("")
    /*val messagePayload = "Hello, MQTT!"*/


/*    val loadConfig = LoadConfig(
        qos = MqttQoS.AT_MOST_ONCE,
        *//*keepAliveSec = 60,*//*
        messagePayloadSize = 500,
        nMessagesPerChannel = 1000,
        channelsPerThread = 100, // the amount of mqtt client connections by thread
        *//*eventLoopsPerGroup = 1, // the amount of thread*//*
        amountOfGroups = 10
    )*/
    val loadConfig = LoadConfig(
    qos = MqttQoS.AT_MOST_ONCE,
        /*keepAliveSec = 60,*/
        messagePayloadSize = 500,
        nMessagesPerChannel = 1000,
        channelsPerThread = 100, // the amount of mqtt client connections by thread
        /*eventLoopsPerGroup = 1, // the amount of thread*/
        amountOfGroups = 10
    )

    val loadTest = LoadTester(
        broker = "localhost",
        port = 1883,
        topic = "test",
        loadConfig
    )

    val info = MessageInfoMetrics(loadConfig)
    info.printParameters()
    loadTest.launch()








}








