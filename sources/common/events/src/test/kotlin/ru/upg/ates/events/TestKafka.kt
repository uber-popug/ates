package ru.upg.ates.events

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.Test
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

//@Testcontainers
class TestKafka {

//    companion object {
//        @JvmStatic
//        @Container
//        val kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
//    }

    // private val bootstrapServers = kafka.bootstrapServers
    private val bootstrapServers = "http://localhost:9994"

    private val producerProps = mapOf(
        "bootstrap.servers" to bootstrapServers,
        "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
        "value.serializer" to "org.apache.kafka.common.serialization.ByteArraySerializer",
        "security.protocol" to "PLAINTEXT"
    )

    private val consumerProps = { group: String ->
        mapOf(
            "bootstrap.servers" to bootstrapServers,
            "auto.offset.reset" to "earliest",
            "key.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer" to "org.apache.kafka.common.serialization.ByteArrayDeserializer",
            "group.id" to group,
            "security.protocol" to "PLAINTEXT"
        )
    }


    @Test
    fun testCheck() {
        produce()
        consume()
    }

    private fun produce() {
        val testRecord = ProducerRecord("test", "1", "Hello world!".encodeToByteArray())

        KafkaProducer<String, ByteArray>(producerProps).use {  producer ->
            producer.send(testRecord) { meta, ex ->
                ex?.printStackTrace()
                println("record sent $meta")
            }
        }
    }

    private fun consume() {
        tailrec fun <T> repeatUntilSome(block: () -> T?): T =
            block() ?: repeatUntilSome(block)

        KafkaConsumer<String, ByteArray>(consumerProps("ates-test")).use { consumer ->
            consumer.subscribe(listOf("test"))

            val message = repeatUntilSome {
                val duration = 400.milliseconds.toJavaDuration()
                consumer.poll(duration)
                    .map { String(it.value()) }
                    .firstOrNull()
            }

            println("received message $message")
        }
    }
}