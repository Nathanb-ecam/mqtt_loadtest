package org.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.netty.handler.codec.mqtt.MqttQoS
import org.example.org.example.MqttCredentials
import org.slf4j.event.Level
import java.util.*
import com.fasterxml.jackson.databind.ObjectMapper


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(CallLogging) {
            level = Level.INFO
        }

        install(CORS) {
            allowHeader(HttpHeaders.ContentType)
            anyHost()
        }
        val token = generateToken()
        println("gen : ${token}")
        val broker_ip = System.getenv("BROKER_IP") ?: "localhost"
        val brokerPortString: String? = System.getenv("BROKER_PORT")
        val broker_port: Int = brokerPortString?.toIntOrNull() ?: 1883
        val topic = System.getenv("BROKER_TOPIC") ?: "test"

        val clientName = System.getenv("MQTT_USERNAME") ?: ""
        val clientPassword = System.getenv("MQTT_PASSWORD") ?: ""
        val mqttCredentials = MqttCredentials(clientName,clientPassword)




        routing {
            get("/") {
                call.respondText("Hello, Ktor!")
            }
            post("/alert"){
                println("Alert")
                call.respondText("Alert")
            }
            post("/launch") {
                var collectTest = false

                val parameters = call.receiveParameters()
                var status = "none"

                val receivedToken = parameters["token"]
                val messagePayloadSize = parameters["messagePayloadSize"]?.toIntOrNull() ?: 0
                val messagePayload = parameters["messagePayload"] ?: ""

                val nMessagesPerChannel = parameters["nMessagesPerChannel"]?.toIntOrNull() ?: 1000
                val channelsPerGroup = parameters["channelsPerGroup"]?.toIntOrNull() ?: 100
                val amountOfGroups = parameters["amountOfGroups"]?.toIntOrNull() ?: 10



                if(receivedToken == token){
                    if(messagePayload.length != 0) collectTest = true
                    if(messagePayload.length == 0 && messagePayloadSize == 0 ) call.respondText("wtf is that query")
                    val loadConfig: LoadConfig?
                    if(collectTest){
                        loadConfig = LoadConfig(
                            qos = MqttQoS.AT_MOST_ONCE,
                            messagePayloadBytes = messagePayload.toByteArray(Charsets.UTF_8),
                            nMessagesPerChannel = nMessagesPerChannel,
                            channelsPerGroup = channelsPerGroup,
                            amountOfGroups = amountOfGroups
                        )
                    }else{
                        loadConfig = LoadConfig(
                            qos = MqttQoS.AT_MOST_ONCE,
                            messagePayloadSize = messagePayloadSize,
                            nMessagesPerChannel = nMessagesPerChannel,
                            channelsPerGroup = channelsPerGroup,
                            amountOfGroups = amountOfGroups
                        )
                    }

                    val loadTest = LoadTester(
                        broker = broker_ip,
                        port = broker_port,
                        topic = topic,
                        mqttCredentials = mqttCredentials,
                        loadConfig
                    )
                    loadTest.launch()

                    val info = MessageInfoMetrics(loadConfig)
                    val parametersInfo = info.loadParameters()
                    println(parametersInfo)
                    status = "Launched ${info.getTheoreticalMessageCount()} messages on $broker_ip:$broker_port, topic : $topic ,  username: ${mqttCredentials.clientName}\n"

                }else{
                    call.respondText("invalid token")
                }

                call.respondText(status)
            }







        }
    }.start(wait = true)
}


fun generateToken(): String {
    val random = Random()
    val bytes = ByteArray(64)
    random.nextBytes(bytes)
    return bytes.joinToString("") { "%02x".format(it) }
}







