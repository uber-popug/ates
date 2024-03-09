package ru.upg.ates.broker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.networknt.schema.InputFormat
import com.networknt.schema.JsonSchema
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import ru.upg.ates.AtesEvent
import ru.upg.ates.Topic
import ru.upg.ates.Event
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

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

        val messages = schema.validate(eventContent, InputFormat.JSON)
        if (messages.isNotEmpty()) {
            val msg = messages.joinToString { it.message }
            throw IllegalArgumentException(
                "event $eventDescription not corresponding " +
                    "schema ${eventDescription.jsonSchemaId} with validation message '$msg'"
            )
        }

        val recordId = System.currentTimeMillis().toString()
        return ProducerRecord(topic.value, recordId, eventContent)
    }


    override fun listener(consumerGroup: String): KafkaListener.KafkaListenerBuilder {
        return KafkaListener.KafkaListenerBuilder(url, consumerGroup, mapper)
    }

    data class EventHandler(
        val kClass: KClass<*>,
        val handler: (Any) -> Unit
    )

    class KafkaListener(
        private val props: Map<String, String>,
        private val mapper: ObjectMapper,
        private val handlers: Map<String, EventHandler>,
    ) : EventsBroker.Listener, Runnable {

        private val log = LoggerFactory.getLogger(javaClass)

        private val consumer = KafkaConsumer<String, ByteArray>(props)
        private val name = props["group.id"]

        override fun listen(): KafkaListener {
            // todo: handle errors
            consumer.subscribe(handlers.keys)
            Thread(this, name).start()
            return this
        }

        override fun run() {
            // todo: configure
            val pollTime = 1000.milliseconds.toJavaDuration()

            while (true) {
                // todo: handle errors
                log.debug("start polling events on listener $name")
                val records = consumer.poll(pollTime)
                if (records.count() > 0)
                    log.info("polled ${records.count()} records on listener $name")

                records.forEach { record: ConsumerRecord<String, ByteArray> ->
                    val topic: String = record.topic()
                    handlers[topic]?.let { (kclass, handler) ->
                        val atesEvent = mapper.readValue<AtesEvent>(record.value())
                        val event = mapper.treeToValue(atesEvent.payload, kclass.java)
                        handler(event)
                    }
                }
            }
        }

        class KafkaListenerBuilder(
            private val kafkaUrl: String,
            private val consumerGroup: String,
            private val mapper: ObjectMapper,
        ) : EventsBroker.Listener.Builder<KafkaListener> {

            private val handlers = mutableMapOf<String, EventHandler>()

            override fun <E : Any> register(
                topic: Topic,
                kclass: KClass<E>,
                handler: (E) -> Unit
            ): KafkaListenerBuilder {
                val targetHandler: (Any) -> Unit = { event: Any ->
                    handler(event as E)
                }

                handlers[topic.value] = EventHandler(kclass, targetHandler)

                return this
            }

            override fun listen(): KafkaListener {
                val props = mapOf(
                    "bootstrap.servers" to kafkaUrl,
                    "auto.offset.reset" to "earliest",
                    "key.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
                    "value.deserializer" to "org.apache.kafka.common.serialization.ByteArrayDeserializer",
                    "group.id" to consumerGroup,
                    "security.protocol" to "PLAINTEXT"
                )

                return KafkaListener(props, mapper, handlers).listen()
            }
        }
    }
}
