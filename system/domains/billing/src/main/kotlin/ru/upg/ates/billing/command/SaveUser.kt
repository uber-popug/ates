package ru.upg.ates.billing.command

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import ru.upg.ates.Command
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.table.UserTable
import ru.upg.ates.events.UserChanged
import ru.upg.ates.events.UserChange

class SaveUser(
    private val event: UserChanged,
) : Command.Silent<BillingContext> {

    override fun execute(context: BillingContext) {
        when (event) {
            is UserChanged.Created -> create(event.payload)
            is UserChanged.Updated -> update(event.payload)
        }
    }

    private fun create(user: UserChange) {
        transaction {
            UserTable.upsert(keys = arrayOf(UserTable.pid)) {
                it[pid] = user.pid
                it[username] = user.username
                it[role] = user.role
            }
        }
    }

    private fun update(user: UserChange) {
        transaction {
            UserTable.update({ UserTable.pid eq user.pid }) {
                it[username] = user.username
                it[role] = user.role
            }
        }
    }
}
