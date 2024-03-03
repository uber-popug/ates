package ru.upg.common.events

interface EventsBroker {
    fun publish(event: Event)
    fun listen(topic: Topic, handler: (Event) -> Unit)
}