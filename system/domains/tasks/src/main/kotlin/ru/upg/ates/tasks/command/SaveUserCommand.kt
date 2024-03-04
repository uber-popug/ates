package ru.upg.ates.tasks.command

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory
import ru.upg.ates.Command
import ru.upg.ates.Event
import ru.upg.ates.events.UserCUD
import ru.upg.ates.events.UserChange
import ru.upg.ates.tasks.TasksDomain

class SaveUserCommand(
    private val event: UserCUD
) : Command<TasksDomain, Unit> {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(domain: TasksDomain): Pair<Unit, List<Event>> {
        when (event) {
            is UserCUD.Created -> create(domain, event.user)
            is UserCUD.Updated -> update(domain, event.user)
            else -> {
                log.warn("not handled UserCUD event ${event.javaClass}")
            }
        }

        return Unit to listOf()
    }

    private fun create(domain: TasksDomain, user: UserChange) {
        transaction {
            domain.tables.users.insert {
                it[pid] = user.pid
                it[username] = user.username
                it[role] = user.role
            }
        }
    }

    private fun update(domain: TasksDomain, user: UserChange) {
        val table = domain.tables.users
        transaction {
            table.update({ table.pid eq user.pid }) {
                it[username] = user.username
                it[role] = user.role
            }
        }
    }
}