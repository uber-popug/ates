package ru.upg.ates.billing.command

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import ru.upg.ates.Command
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.table.UserTable
import ru.upg.ates.events.UserCreated

class SaveUser(
    private val event: UserCreated
) : Command.Silent<BillingContext> {

    override fun execute(context: BillingContext) {
        transaction {
            UserTable.upsert(keys = arrayOf(UserTable.pid)) {
                it[pid] = event.pid
                it[username] = event.username
                it[role] = event.role
            }
        }
    }
}
