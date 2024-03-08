package ru.upg.ates

import ru.upg.ates.broker.EventsBroker
import kotlin.reflect.KClass


interface BoundedContext {
    val broker: EventsBroker
    val notFoundTopic: Topic
    val card: Map<KClass<out Event>, Topic>

    fun publish(event: Event) {
        val topic = card[event::class] ?: notFoundTopic
        broker.publish(topic, event)
    }
}

fun <D : BoundedContext, R> D.execute(command: Command<D, R>): R {
    return command.execute(this)
}

fun <D : BoundedContext, E, C : Command<D, *>> D.handler(
    commandConstructor: (E) -> C
): (E) -> Unit {
    return { event: E ->
        val command = commandConstructor(event)
        this.execute(command)
    }
}
