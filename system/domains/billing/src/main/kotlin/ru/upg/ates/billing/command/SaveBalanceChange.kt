package ru.upg.ates.billing.command

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.model.Task
import ru.upg.ates.billing.model.User
import ru.upg.ates.billing.table.BalanceChangeTable
import ru.upg.ates.events.BalanceChanged
import java.time.Instant

class SaveBalanceChange(
    private val user: User,
    private val task: Task?,
    private val event: BalanceChanged,
) : Command.Silent<BillingContext> {

    override fun execute(context: BillingContext) {
        transaction {
            BalanceChangeTable.insert {
                it[userId] = user.id
                it[taskId] = task?.id
                it[description] = event.description
                it[reason] = event.reason
                it[income] = event.income
                it[outcome] = event.outcome
                it[createdAt] = Instant.now()
            }

            context.publish(event)
        }
    }
}
