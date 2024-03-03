package ru.upg.cqrs

import ru.upg.ates.AtesKafka


interface Domain<D : Domain<D>> {
    val kafka: AtesKafka
}


fun <D : Domain<D>> D.execute(command: Command<D, *>) {
    command.execute(this).forEach { event ->
        eventsListener(event)
    }
}

fun <D : Domain<D>, T : ReadModel> D.execute(query: Query<D, T>): T {
    return query.execute(this)
}
