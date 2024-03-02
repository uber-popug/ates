package ru.upg.ates.operation

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import ru.upg.ates.AtesEvent

class ProduceEvent(
    private val kafkaServer: String
) {

    companion object {
        private val producerProps = { server: String ->
            mapOf(
                "bootstrap.servers" to server,
                "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
                "value.serializer" to "org.apache.kafka.common.serialization.ByteArraySerializer",
                "security.protocol" to "PLAINTEXT"
            )
        }
    }


    operator fun invoke(topic: String, event: AtesEvent): RecordMetadata {
        val mapper = jacksonObjectMapper()
        val content = mapper.writeValueAsBytes(event)

        val recordId = System.currentTimeMillis().toString()
        val record = ProducerRecord(topic, recordId, content)

        val props = producerProps(kafkaServer)
        KafkaProducer<String, ByteArray>(props).use { producer ->
            return producer.send(record).get()
        }
    }
}