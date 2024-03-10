package ru.upg.ates.analytic.command

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import ru.upg.ates.Command
import ru.upg.ates.analytic.AnalyticContext
import ru.upg.ates.analytic.table.UserTable
import ru.upg.ates.events.UserCreated

class SaveUser(
    private val event: UserCreated
) : Command.Silent<AnalyticContext> {

    override fun execute(context: AnalyticContext) {
        transaction {
            UserTable.upsert(keys = arrayOf(UserTable.pid)) {
                it[pid] = event.pid
                it[username] = event.username
                it[role] = event.role
            }
        }
    }
}
