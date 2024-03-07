package ru.upg.ates.events.broker

import ru.upg.ates.Topic
import ru.upg.ates.events.Event
import kotlin.reflect.KClass

interface EventsBroker {
    fun publish(topic: Topic, event: Event<*>)

    fun listener(consumerGroup: String): Listener.Builder<*>


    interface Listener {
        fun listen(): Listener

        interface Builder<out L : Listener> {
            fun <E : Event<*>> register(
                topic: Topic,
                kclass: KClass<E>,
                handler: (E) -> Unit,
            ) : Builder<L>

            fun listen(): L
        }
    }
}
