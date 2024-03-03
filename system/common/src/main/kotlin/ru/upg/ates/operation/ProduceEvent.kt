package ru.upg.ates.operation

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import ru.upg.ates.AtesEvent
import java.util.concurrent.Future

class ProduceEvent(
    private val kafkaServer: String
):AutoCloseable {

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

    private val props = producerProps(kafkaServer)
    private val producer = KafkaProducer<String, ByteArray>(props)

    operator fun invoke(topic: String, event: AtesEvent): Future<RecordMetadata> {
        val mapper = jacksonObjectMapper()
        val content = mapper.writeValueAsBytes(event)

        val recordId = System.currentTimeMillis().toString()
        val record = ProducerRecord(topic, recordId, content)

        return producer.send(record)
    }

    override fun close() {
        producer.close()
    }
}