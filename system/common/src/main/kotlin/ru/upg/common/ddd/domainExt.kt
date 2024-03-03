package ru.upg.common.ddd

import ru.upg.common.cqrs.Command
import ru.upg.common.cqrs.Query
import ru.upg.common.cqrs.ReadModel

fun <D : Domain<D>> D.execute(command: Command<D, *>) {
    command.execute(this).forEach { event ->
        broker.publish(event)
    }
}

fun <D : Domain<D>, T : ReadModel> D.execute(query: Query<D, T>): T {
    return query.execute(this)
}
