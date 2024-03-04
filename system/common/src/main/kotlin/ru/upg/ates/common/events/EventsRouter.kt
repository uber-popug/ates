package ru.upg.ates.common.events

interface EventsRouter {
    fun route(event: Event): Topic
}
