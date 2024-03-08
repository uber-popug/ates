package ru.upg.ates.auth

import org.jetbrains.exposed.sql.Database
import ru.upg.ates.AtesTopic
import ru.upg.ates.BoundedContext
import ru.upg.ates.Topic
import ru.upg.ates.auth.table.UserTable
import ru.upg.ates.events.Event
import ru.upg.ates.events.UserChanged
import ru.upg.ates.broker.EventsBroker
import kotlin.reflect.KClass

class AuthDomain(
    override val broker: EventsBroker,
    val database: Database,
    val tables: Tables,
) : BoundedContext {

    // event routing settings

    override val notFoundTopic = AtesTopic.NOT_FOUND

    override val card: Map<KClass<out Event<*>>, Topic> = mapOf(
        UserChanged.Created::class to AtesTopic.USERS,
        UserChanged.Updated::class to AtesTopic.USERS,
    )

    // persistence settings

    class Tables(
        val users: UserTable
    )
}
