package ru.upg.ates.tasks

import ru.upg.ates.Topic
import ru.upg.ates.BoundedContext
import ru.upg.ates.broker.EventsBroker
import ru.upg.ates.events.TaskAssigned
import ru.upg.ates.events.TaskCreated
import ru.upg.ates.events.TaskFinished
import ru.upg.ates.events.UserCreated
import ru.upg.ates.handler
import ru.upg.ates.tasks.command.SaveUser

class TasksContext(
    val serviceName: String, 
    override val broker: EventsBroker
) : BoundedContext {

    override val notFoundTopic = Topic.NOT_FOUND

    override val card = mapOf(
        TaskCreated::class to Topic.TASKS,
        TaskAssigned::class to Topic.TASK_ASSIGNED,
        TaskFinished::class to Topic.TASK_FINISHED
    )

    private val listener = broker.listener(serviceName)
        .register(Topic.USERS, UserCreated::class, handler(::SaveUser))
        .listen()
}
