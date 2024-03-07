package ru.upg.ates.events.broker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.networknt.schema.InputFormat
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import ru.upg.ates.Topic
import ru.upg.ates.events.Event
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

class KafkaEventsBroker(
    val url: String,
    val jsonSchemas: Map<String, String>,
    val mapper: ObjectMapper,
) : EventsBroker {

    private val log = LoggerFactory.getLogger(javaClass)

    private val producerProps = mapOf(
        "bootstrap.servers" to url,
        "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
        "value.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
        "security.protocol" to "PLAINTEXT"
    )

    private val producer = KafkaProducer<String, String>(producerProps)


    override fun publish(topic: Topic, event: Event<*>) {
        val record = makeRecord(topic, event)
        producer.send(record).get()
    }

    private fun makeRecord(topic: Topic, event: Event<*>): ProducerRecord<String, String> {
        val schemaMapper = ObjectMapper(YAMLFactory())

        val factory =
            JsonSchemaFactory
                .builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4))
                .yamlMapper(schemaMapper)
                .jsonMapper(schemaMapper)
                .build()

        val schemaContent  = jsonSchemas[event.jsonSchemaId]!! // fixme
        val schemaNode = schemaMapper.readTree(schemaContent)
        val schema = factory.getSchema(schemaNode)

        val eventContent = mapper.writeValueAsString(event)
        val messages = schema.validate(eventContent, InputFormat.JSON)
        if (messages.isNotEmpty()) {
            val msg = messages.joinToString { it.message }
            throw IllegalArgumentException(
                "event ${event.name}${event.version} not corresponding " +
                "schema ${event.jsonSchemaId} with validation message '$msg'"
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
                        val event = mapper.readValue(record.value(), kclass.java)
                        handler(event)
                    }
                }
            }
        }

        class KafkaListenerBuilder(
            private val kafkaUrl: String,
            private val consumerGroup: String,
            private val mapper: ObjectMapper,
        ): EventsBroker.Listener.Builder<KafkaListener> {

            private val handlers = mutableMapOf<String, EventHandler>()

            override fun <E : Event<*>> register(
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
