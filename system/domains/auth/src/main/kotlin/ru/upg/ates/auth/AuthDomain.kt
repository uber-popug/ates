package ru.upg.ates.auth

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.AtesTopic
import ru.upg.ates.Domain
import ru.upg.ates.Topic
import ru.upg.ates.auth.table.UserTable
import ru.upg.ates.events.Event
import ru.upg.ates.events.UserCUD
import ru.upg.ates.events.broker.EventsBroker
import ru.upg.ates.events.broker.KafkaEventsBroker
import ru.upg.ates.model.DomainConfig
import kotlin.reflect.KClass

class AuthDomain(
    override val broker: EventsBroker,
    val database: Database,
    val tables: Tables,
) : Domain {

    // event routing settings

    override val notFoundTopic = AtesTopic.NOT_FOUND

    override val card: Map<KClass<out Event<*>>, Topic> = mapOf(
        UserCUD.Created::class to AtesTopic.USERS,
        UserCUD.Updated::class to AtesTopic.USERS,
    )

    // persistence settings

    class Tables(
        val users: UserTable
    )
}
