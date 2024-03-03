package ru.upg.ates

import ru.upg.cqrs.Change

interface KafkaChange<T : Any> : Change {
    val topic: AtesTopic<T>
    val event: T
}