package ru.upg.ates.billing.command

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import ru.upg.ates.Command
import ru.upg.ates.billing.BillingDomain
import ru.upg.ates.events.Event
import ru.upg.ates.events.TaskCUD
import ru.upg.ates.events.TaskChange

class SaveTaskCommand(
    private val event: TaskCUD
): Command<BillingDomain, Unit> {

    override fun execute(domain: BillingDomain): Pair<Unit, List<Event<*>>> {
        when (event) {
            is TaskCUD.Created -> create(domain, event.payload)
            is TaskCUD.Updated -> update(domain, event.payload)
        }

        return Unit to listOf()
    }

    private fun create(domain: BillingDomain, change: TaskChange) {
        transaction {
            domain.tables.tasks.let { users ->
                users.upsert(keys = arrayOf(users.pid)) {
                    it[pid] = change.pid
                    it[name] = change.name
                    it[assignPrice] = (1..20).random()
                    it[finished] = change.finished
                }
            }
        }
    }

    private fun update(domain: BillingDomain, change: TaskChange) {
        val table = domain.tables.tasks
        transaction {
            table.update({ table.pid eq change.pid }) {
                it[pid] = change.pid
                it[name] = change.name
            }
        }
    }
}
