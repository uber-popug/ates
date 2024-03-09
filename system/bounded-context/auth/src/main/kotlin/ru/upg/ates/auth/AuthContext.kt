package ru.upg.ates.auth

import org.jetbrains.exposed.sql.Database
import ru.upg.ates.AtesTopic
import ru.upg.ates.BoundedContext
import ru.upg.ates.Event
import ru.upg.ates.Topic
import ru.upg.ates.broker.EventsBroker
import ru.upg.ates.events.UserCreated
import kotlin.reflect.KClass

class AuthContext(
    override val broker: EventsBroker,
    val database: Database,
) : BoundedContext {

    override val notFoundTopic = AtesTopic.NOT_FOUND

    override val card: Map<KClass<out Event>, Topic> = mapOf(
        UserCreated::class to AtesTopic.USERS,
    )
}
