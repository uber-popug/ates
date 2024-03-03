package ru.upg.common.events

class SimpleEventsRouter(
    private val card: Map<Event, Topic>,

    /**
     * topic where goes all events which are not registered at card
     */
    private val notFoundTopic: Topic
) : EventsRouter {

    override fun route(event: Event): Topic {
        return card[event] ?: notFoundTopic
    }
}