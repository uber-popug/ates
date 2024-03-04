package ru.upg.ates.common.ddd

import ru.upg.ates.common.cqrs.Command
import ru.upg.ates.common.cqrs.IAggregate
import ru.upg.ates.common.cqrs.Query
import ru.upg.ates.common.cqrs.ReadModel
import ru.upg.ates.common.events.Event

fun <D : Domain<D>, R> D.execute(command: Command<D, *, R>): R {
    val (result, events) = command.execute(this)

    events.forEach { event ->
        broker.publish(event)
    }

    return result
}

fun <D : Domain<D>, T : ReadModel> D.fetch(query: Query<D, T>): T {
    return query.execute(this)
}

fun <D : Domain<D>, E, R> D.handler(
    constructor: (E) -> Command<D, E, R>
): (E) -> Unit where E : Event, E : IAggregate {
    return { event: E -> execute(constructor(event)) }
}