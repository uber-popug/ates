package ru.upg.ates


interface Domain<D : Domain<D>> {
    val broker: EventsBroker
}

fun <D : Domain<D>, R> D.execute(command: Command<D, R>): R {
    val (result, events) = command.execute(this)

    events.forEach { event ->
        broker.publish(event)
    }

    return result
}

fun <D : Domain<D>, T> D.fetch(query: Query<D, T>): T {
    return query.execute(this)
}
