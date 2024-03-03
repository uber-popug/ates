package ru.upg.ates.event

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.Test
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

    private val producer = KafkaProducer<String, ByteArray>(producerProps)


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

    private val consumer = KafkaConsumer<String, ByteArray>(consumerProps("ates-test"))


    @Test
    fun testCheck() {
        produce("test1")
        consume("test1")
        produce("test2")
        consume("test2")

        producer.close()
        consumer.close()
    }

    private fun produce(topic: String) {
        val testRecord = ProducerRecord(topic, "1", "Hello world!".encodeToByteArray())

        producer.send(testRecord) { meta, ex ->
            ex?.printStackTrace()
            println("record sent $meta")
        }
    }

    private fun consume(topic: String) {
        tailrec fun <T> repeatUntilSome(block: () -> T?): T =
            block() ?: repeatUntilSome(block)

        consumer.subscribe(listOf(topic))

        val message = repeatUntilSome {
            val duration = 400.milliseconds.toJavaDuration()
            consumer.poll(duration)
                .map { String(it.value()) }
                .firstOrNull()
        }

        println("received message $message")
    }
}