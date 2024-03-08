package ru.upg.ates.billing.command

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import ru.upg.ates.Command
import ru.upg.ates.billing.BillingDomain
import ru.upg.ates.events.Event
import ru.upg.ates.events.UserCUD
import ru.upg.ates.events.UserChange

class SaveUserCommand(
    private val event: UserCUD,
) : Command<BillingDomain, Unit> {

    override fun execute(domain: BillingDomain): Pair<Unit, List<Event<*>>> {
        when (event) {
            is UserCUD.Created -> create(domain, event.payload)
            is UserCUD.Updated -> update(domain, event.payload)
        }

        return Unit to listOf()
    }

    private fun create(domain: BillingDomain, user: UserChange) {
        transaction {
            domain.tables.users.let { users ->
                users.upsert(keys = arrayOf(users.pid)) {
                    it[pid] = user.pid
                    it[username] = user.username
                    it[role] = user.role
                }
            }
        }
    }

    private fun update(domain: BillingDomain, user: UserChange) {
        val table = domain.tables.users
        transaction {
            table.update({ table.pid eq user.pid }) {
                it[username] = user.username
                it[role] = user.role
            }
        }
    }
}
