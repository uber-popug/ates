package ru.upg.ates

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

class KafkaEventsBroker(
    val url: String,
    val notFoundTopic: Topic,
    val card: Map<KClass<out Event>, Topic>
) : EventsBroker {

    private val mapper = jacksonObjectMapper()

    private val producerProps = mapOf(
        "bootstrap.servers" to url,
        "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
        "value.serializer" to "org.apache.kafka.common.serialization.ByteArraySerializer",
        "security.protocol" to "PLAINTEXT"
    )

    private val producer = KafkaProducer<String, ByteArray>(producerProps)


    override fun publish(event: Event) {
        val topic = card[event::class] ?: notFoundTopic
        val record = makeRecord(topic, event)
        producer.send(record).get()
    }

    private fun makeRecord(topic: Topic, event: Event): ProducerRecord<String, ByteArray> {
        val content = mapper.writeValueAsBytes(event)
        val recordId = System.currentTimeMillis().toString()
        return ProducerRecord(topic.value, recordId, content)
    }


    override fun listener(consumerGroup: String): KafkaListener.KafkaListenerBuilder {
        return KafkaListener.KafkaListenerBuilder(url, consumerGroup)
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
        ): EventsBroker.Listener.Builder<KafkaListener> {

            private val handlers = mutableMapOf<String, EventHandler>()

            override fun <E : Event> register(
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

                val mapper = jacksonObjectMapper()

                return KafkaListener(props, mapper, handlers).listen()
            }
        }
    }
}