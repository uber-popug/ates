package ru.upg.cqrs


interface Domain<D : Domain<D>> {
    val eventsListener: (Event) -> Unit
}


fun <D : Domain<D>> D.execute(command: Command<D, *>) {
    command.execute(this).forEach { event ->
        eventsListener(event)
    }
}

fun <D : Domain<D>, T : ReadModel> D.execute(query: Query<D, T>): T {
    return query.execute(this)
}
