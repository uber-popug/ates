package ru.upg.ates

import ru.upg.cqrs.Event

fun interface EventsListener : (Event) -> Unit {
    fun onEvent(event: Event)

    override fun invoke(event: Event) {
        onEvent(event)
    }
}