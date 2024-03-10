package ru.upg.ates.broker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.networknt.schema.JsonSchema
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.slf4j.LoggerFactory
import ru.upg.ates.AtesEvent
import ru.upg.ates.Topic
import ru.upg.ates.schema.validate
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

class KafkaListener(
    private val props: Map<String, String>,
    private val mapper: ObjectMapper,
    private val handlers: Map<String, EventHandler>,
    private val jsonSchemas: Map<String, JsonSchema>,
) : EventsBroker.Listener, Runnable {

    data class EventHandler(
        val kClass: KClass<*>,
        val handler: (Any) -> Unit
    )
    
    private val log = LoggerFactory.getLogger(javaClass)

    private val consumer = KafkaConsumer<String, String>(props)
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
            try {
                log.debug("start polling events on listener $name")
                val records = consumer.poll(pollTime)
                if (records.count() > 0)
                    log.info("polled ${records.count()} records on listener $name")

                processRecords(records).also { commits ->
                    consumer.commitSync(commits)
                    commits.forEach { (topicPartition, offset) ->
                        consumer.seek(topicPartition, offset)
                    }    
                }
            } catch (ex: Exception) {
                log.error("exception while listening topics $name", ex)
            }
        }
    }
    
    private fun processRecords(
        records: Iterable<ConsumerRecord<String, String>>
    ): MutableMap<TopicPartition, OffsetAndMetadata> {
        
        return mutableMapOf<TopicPartition, OffsetAndMetadata>().also { processedRecords ->
            // group fetched records by topic to process all events of the topic
            records.groupBy { it.topic() }.forEach { (topic, records) ->
                
                // be sure that events are processed from earliest to oldest
                records.sortedBy { it.offset() }.forEach { record ->
                    val str = "record $topic(${record.key()})"
                    val tp = TopicPartition(record.topic(), record.partition())
                    
                    try {
                        log.info("start processing $str")
                        val (kclass, handler) = handlers[topic] ?: throw IllegalStateException(
                            "Subscription to topic '$topic' without handler"
                        )

                        val atesEvent = validateSchema(record)
                        val event = mapper.treeToValue(atesEvent.payload, kclass.java)
                        handler(event)

                        log.info("$str successfully processed")
                        
                        // +1 by doc to poll the next records
                        processedRecords[tp] = OffsetAndMetadata(record.offset() + 1) 
                    } catch (ex: Exception) {
                        log.error("exception while processing $str '${ex.message}'")
                        
                        // on the next poll would be received same record
                        processedRecords[tp] = OffsetAndMetadata(record.offset())
                    }
                }
            }
        }
    }
    
    private fun validateSchema(record: ConsumerRecord<String, String>): AtesEvent {
        val eventContent = record.value()
        val eventTree = mapper.readTree(eventContent)
        val schemaId = eventTree["jsonSchemaId"]?.asText() ?: throw IllegalArgumentException(
            "Not found 'jsonSchemaId' field on event ${record.key()} in topic ${record.topic()}"
        )
        val schema = jsonSchemas[schemaId] ?: throw IllegalArgumentException(
            "Not found json schema for id $schemaId"
        )
        schema.validate(eventContent, "for event ${record.key()} in topic ${record.topic()} ")

        return mapper.treeToValue<AtesEvent>(eventTree)
    }
    

    class KafkaListenerBuilder(
        private val kafkaUrl: String,
        private val consumerGroup: String,
        private val mapper: ObjectMapper,
        private val jsonSchemas: Map<String, JsonSchema>,
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
                "value.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
                "group.id" to consumerGroup,
                "security.protocol" to "PLAINTEXT",
                "enable.auto.commit" to "false"
            )

            return KafkaListener(props, mapper, handlers, jsonSchemas).listen()
        }
    }
}
