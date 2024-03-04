package ru.upg.ates


interface Domain {
    val broker: EventsBroker
}

fun <D : Domain, R> D.execute(command: Command<D, R>): R {
    val (result, events) = command.execute(this)

    events.forEach { event ->
        broker.publish(event)
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
