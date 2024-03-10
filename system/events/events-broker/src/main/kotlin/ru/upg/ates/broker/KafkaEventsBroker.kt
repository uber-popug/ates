package ru.upg.ates.broker

import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.InputFormat
import com.networknt.schema.JsonSchema
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import ru.upg.ates.AtesEvent
import ru.upg.ates.Topic
import ru.upg.ates.Event
import ru.upg.ates.schema.validate
import kotlin.reflect.full.findAnnotation

class KafkaEventsBroker(
    private val url: String,
    private val jsonSchemas: Map<String, JsonSchema>,
    private val mapper: ObjectMapper,
) : EventsBroker {

    private val log = LoggerFactory.getLogger(javaClass)

    private val producerProps = mapOf(
        "bootstrap.servers" to url,
        "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
        "value.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
        "security.protocol" to "PLAINTEXT"
    )

    private val producer = KafkaProducer<String, String>(producerProps)


    override fun <E : Any> publish(producerName: String, topic: Topic, event: E) {
        val record = makeRecord(producerName, topic, event)
        producer.send(record).get()
    }

    private fun <E : Any> makeRecord(producer: String, topic: Topic, event: E): ProducerRecord<String, String> {
        val eventDescription = event::class.findAnnotation<Event>() ?: throw IllegalArgumentException(
            "Not found annotation Event on payload ${event::class}"
        )

        val targetEvent = AtesEvent(
            jsonSchemaId = eventDescription.jsonSchemaId,
            name = eventDescription.name,
            version = eventDescription.version,
            producer = producer,
            payload = mapper.valueToTree(event)
        )

        val eventContent = mapper.writeValueAsString(targetEvent)

        val schema = jsonSchemas[eventDescription.jsonSchemaId] ?: throw IllegalArgumentException(
            "Not found json schema for id ${eventDescription.jsonSchemaId}"
        )
        schema.validate(eventContent, "for event $eventDescription ")

        val recordId = System.currentTimeMillis().toString()
        return ProducerRecord(topic.value, recordId, eventContent)
    }


    override fun listener(consumerGroup: String): KafkaListener.KafkaListenerBuilder {
        return KafkaListener.KafkaListenerBuilder(url, consumerGroup, mapper, jsonSchemas)
    }
}
