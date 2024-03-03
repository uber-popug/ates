package ru.upg.ates.tasks.commands

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory
import ru.upg.ates.event.UserChange
import ru.upg.ates.event.UserCUD
import ru.upg.ates.tasks.TasksDomain
import ru.upg.common.cqrs.Command
import ru.upg.common.events.Event

class SaveUser(
    override val aggregate: UserCUD
) : Command<TasksDomain, UserCUD>() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(domain: TasksDomain): List<Event> {
        when (aggregate) {
            is UserCUD.Created -> create(domain, aggregate.user)
            is UserCUD.Updated -> update(domain, aggregate.user)
            else -> {
                log.warn("not handled UserCUD event ${aggregate.javaClass}")
            }
        }

        return listOf()
    }

    private fun create(domain: TasksDomain, user: UserChange) {
        transaction {
            domain.tables.users.insert {
                it[id] = user.pid
                it[username] = user.username
                it[role] = user.role
            }
        }
    }

    private fun update(domain: TasksDomain, user: UserChange) {
        val table = domain.tables.users
        transaction {
            table.update({ table.id eq user.pid }) {
                it[username] = user.username
                it[role] = user.role
            }
        }
    }
}