package ru.upg.ates.analytic.command

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.analytic.AnalyticContext
import ru.upg.ates.analytic.query.GetTask
import ru.upg.ates.analytic.query.GetUser
import ru.upg.ates.analytic.table.BalanceChangeTable
import ru.upg.ates.events.BalanceChanged
import ru.upg.ates.execute
import java.time.Instant

class SaveBalanceChanged(
    private val event: BalanceChanged,
) : Command.Silent<AnalyticContext> {

    override fun execute(context: AnalyticContext): Unit = with(context) {
        transaction {
            val user = execute(GetUser(event.userPid))
            val task = event.taskPid?.let { execute(GetTask(it)) }
            
            BalanceChangeTable.insert {
                it[userId] = user.id
                it[taskId] = task?.id
                it[description] = event.description
                it[reason] = event.reason
                it[income] = event.income
                it[outcome] = event.outcome
                it[createdAt] = Instant.now()
            }
        }
    }
}
