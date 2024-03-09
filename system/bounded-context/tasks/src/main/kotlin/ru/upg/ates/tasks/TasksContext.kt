package ru.upg.ates.tasks

import ru.upg.ates.BoundedContext
import ru.upg.ates.Topic
import ru.upg.ates.broker.EventsBroker
import ru.upg.ates.events.UserCreated
import ru.upg.ates.handler
import ru.upg.ates.tasks.command.SaveUser

class TasksContext(
    override val serviceName: String,
    override val broker: EventsBroker
) : BoundedContext {

    private val listener = broker.listener(serviceName)
        .register(Topic.USERS, UserCreated::class, handler(::SaveUser))
        .listen()
}
