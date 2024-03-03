package ru.upg.common.events

interface EventsRouter {
    fun route(event: Event): Topic
}
