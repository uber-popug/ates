package ru.upg.ates

import ru.upg.ates.events.Event
import ru.upg.ates.events.broker.EventsBroker
import kotlin.reflect.KClass


interface Domain {
    val broker: EventsBroker
    val notFoundTopic: Topic
    val card: Map<KClass<out Event<*>>, Topic>
}


fun <D : Domain, R> D.execute(command: Command<D, R>): R {
    val (result, events) = command.execute(this)

    events.forEach { event ->
        val topic = card[event::class] ?: notFoundTopic
        broker.publish(topic, event)
    }

    return result
}

fun <D : Domain, T> D.fetch(query: Query<D, T>): T {
    return query.execute(this)
}

fun <D : Domain, E, C : Command<D, *>> D.handler(
    commandConstructor: (E) -> C
): (E) -> Unit {
    return { event: E ->
        val command = commandConstructor(event)
        this.execute(command)
    }
}
