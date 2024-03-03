package ru.upg.ates.event

import org.apache.kafka.clients.producer.RecordMetadata
import ru.upg.cqrs.Event
import ru.upg.ates.KafkaChange

data class KafkaEventProcessed(
    val change: KafkaChange<*>,
    val result: Result<RecordMetadata>
) : Event