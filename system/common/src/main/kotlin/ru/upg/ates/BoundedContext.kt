package ru.upg.ates

import ru.upg.ates.broker.EventsBroker


interface BoundedContext {
    val serviceName: String
    val broker: EventsBroker

    fun <E : Any> publish(topic: Topic, event: E) {
        broker.publish(serviceName, topic, event)
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
