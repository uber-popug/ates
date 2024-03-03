package ru.upg.common.events

import kotlin.reflect.KClass

interface EventsBroker {
    fun publish(event: Event)

    fun listen(consumerGroup: String): Listener


    interface Listener {

        fun <E : Event> register(
            topic: Topic,
            kclass: KClass<E>,
            handler: (E) -> Unit
        )

        fun start()
    }
}