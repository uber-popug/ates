package ru.upg.ates

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import ru.upg.cqrs.Event

class AtesKafka(val mapper: ObjectMapper, url: String): EventsListener {

    private val producer = KafkaProducer<String, ByteArray>(
        mapOf(
            "bootstrap.servers" to url,
            "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
            "value.serializer" to "org.apache.kafka.common.serialization.ByteArraySerializer",
            "security.protocol" to "PLAINTEXT"
        )
    )

    override fun onEvent(event: Event) {
            val content = mapper.writeValueAsBytes(change.event)

            val recordId = System.currentTimeMillis().toString()
            val record = ProducerRecord(change.topic.value, recordId, content)

            producer.send(record).get()
        }
    }
}