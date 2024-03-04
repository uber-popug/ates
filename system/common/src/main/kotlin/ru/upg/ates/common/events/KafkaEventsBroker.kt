package ru.upg.ates.common.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
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


    override fun listen(consumerGroup: String): Listener {
        return Listener(url, consumerGroup, mapper)
    }


    class Listener(
        kafkaUrl: String,
        private val consumerGroup: String,
        private val mapper: ObjectMapper
    ) : EventsBroker.Listener, Runnable {

        data class EventHandler(
            val kClass: KClass<*>,
            val handler: (Any) -> Unit
        )

        private val log = LoggerFactory.getLogger(javaClass)

        private val consumerProps = mapOf(
            "bootstrap.servers" to kafkaUrl,
            "auto.offset.reset" to "earliest",
            "key.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer" to "org.apache.kafka.common.serialization.ByteArrayDeserializer",
            "group.id" to consumerGroup,
            "security.protocol" to "PLAINTEXT"
        )

        private val consumer = KafkaConsumer<String, ByteArray>(consumerProps)
        private val handlers = mutableMapOf<String, EventHandler>()

        override fun <E : Event> register(
            topic: Topic,
            kclass: KClass<E>,
            handler: (E) -> Unit
        ) {
            val targetHandler: (Any) -> Unit = { event: Any ->
                handler(event as E)
            }

            handlers[topic.value] = EventHandler(kclass, targetHandler)

            consumer.subscribe(handlers.keys)
        }


        override fun start() {
            Thread(this, consumerGroup).start()
        }

        override fun run() {
            // todo configure
            val pollTime = 500.milliseconds.toJavaDuration()

            while (true) {
                log.info("start polling events $consumerGroup")
                consumer.poll(pollTime).forEach { record: ConsumerRecord<String, ByteArray> ->
                    val topic: String = record.topic()
                    handlers[topic]?.let { (kclass, handler) ->
                        val event = mapper.readValue(record.value(), kclass.java)
                        handler(event)
                    }
                }
            }
        }
    }
}