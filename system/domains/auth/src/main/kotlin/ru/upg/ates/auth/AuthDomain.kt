package ru.upg.ates.auth

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.AtesTopic
import ru.upg.ates.auth.table.UserTable
import ru.upg.ates.events.UserCUD
import ru.upg.ates.model.DomainConfig
import ru.upg.ates.Domain
import ru.upg.ates.KafkaEventsBroker

class AuthDomain(
    val tables: Tables,
    val config: DomainConfig
) : Domain {

    // event broker settings

    override val broker = KafkaEventsBroker(
        url = config.kafkaUrl,
        notFoundTopic = AtesTopic.NOT_FOUND,
        card = mapOf(
            UserCUD.Created::class to AtesTopic.USERS,
            UserCUD.Updated::class to AtesTopic.USERS,
        )
    )


    // persistence settings

    class Tables(
        val users: UserTable
    )

    private val database = config.db.run {
        Database.connect(
            url = url,
            user = username,
            password = password
        )

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                tables.users
            )
        }
    }
}