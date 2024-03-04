package ru.upg.ates.auth

import ru.upg.ates.AtesTopic
import ru.upg.ates.auth.table.UserTable
import ru.upg.ates.event.UserCUD
import ru.upg.common.ddd.Domain
import ru.upg.common.events.KafkaEventsBroker

class AuthDomain(
    kafkaUrl: String,
    val tables: Tables
) : Domain<AuthDomain> {

    class Tables(
        val userTable: UserTable
    )

    override val broker = KafkaEventsBroker(
        url = kafkaUrl,
        notFoundTopic = AtesTopic.NOT_FOUND,
        card = mapOf(
            UserCUD.Created::class to AtesTopic.USERS,
            UserCUD.Updated::class to AtesTopic.USERS,
        )
    )
}