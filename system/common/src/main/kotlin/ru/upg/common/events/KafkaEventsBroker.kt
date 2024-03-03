package ru.upg.common.events

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.concurrent.ConcurrentHashMap

class KafkaEventsBroker(
    private val url: String,
    private val consumerGroup: String,
    private val card: Map<Event, Topic>,
    private val notFoundTopic: Topic,
    private val mapper: ObjectMapper
) : EventsBroker {

    private val producerProps = mapOf(
        "bootstrap.servers" to url,
        "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
        "value.serializer" to "org.apache.kafka.common.serialization.ByteArraySerializer",
        "security.protocol" to "PLAINTEXT"
    )

    private val producer = KafkaProducer<String, ByteArray>(producerProps)


    override fun publish(event: Event) {
        val topic = card[event] ?: notFoundTopic
        val record = makeRecord(topic, event)
        producer.send(record).get()
    }

    private fun makeRecord(topic: Topic, event: Event): ProducerRecord<String, ByteArray> {
        val content = mapper.writeValueAsBytes(event)
        val recordId = System.currentTimeMillis().toString()
        return ProducerRecord(topic.value, recordId, content)
    }


    private val consumerProps = mapOf(
        "bootstrap.servers" to url,
        "auto.offset.reset" to "earliest",
        "key.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
        "value.deserializer" to "org.apache.kafka.common.serialization.ByteArrayDeserializer",
        "group.id" to consumerGroup,
        "security.protocol" to "PLAINTEXT"
    )

    private val consumer = KafkaConsumer<String, ByteArray>(consumerProps)

    private val listeningTopics = ConcurrentHashMap<Topic, (Event) -> Unit>()

    override fun listen(topic: Topic, handler: (Event) -> Unit) {
        listeningTopics[topic] = handler
        consumer.subscribe(listeningTopics.map(Topic::value))
    }
}