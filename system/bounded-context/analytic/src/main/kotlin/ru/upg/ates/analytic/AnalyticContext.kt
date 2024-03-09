package ru.upg.ates.analytic

import ru.upg.ates.Topic
import ru.upg.ates.BoundedContext
import ru.upg.ates.Event
import ru.upg.ates.analytic.command.SaveBalanceChanged
import ru.upg.ates.analytic.command.SaveTask
import ru.upg.ates.analytic.command.SaveUser
import ru.upg.ates.broker.EventsBroker
import ru.upg.ates.events.BalanceChanged
import ru.upg.ates.events.TaskCreated
import ru.upg.ates.events.UserCreated
import ru.upg.ates.handler
import kotlin.reflect.KClass

class AnalyticContext(
    val serviceName: String,
    override val broker: EventsBroker
) : BoundedContext, EventsBroker by broker {

    override val notFoundTopic = Topic.NOT_FOUND

    override val card: Map<KClass<out Event>, Topic> = emptyMap()
    
    private val listener = broker.listener(serviceName)
        .register(Topic.USERS, UserCreated::class, handler(::SaveUser))
        .register(Topic.TASKS, TaskCreated::class, handler(::SaveTask))
        .register(Topic.BALANCE_CHANGES, BalanceChanged::class, handler(::SaveBalanceChanged))
}
