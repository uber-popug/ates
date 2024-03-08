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
    private val change: BalanceChanged.Payload
): Command<BillingContext, BalanceChanged> {

    override fun execute(context: BillingContext): BalanceChanged {
        return transaction {
            val now = Instant.now()

            BalanceChangeTable.insert {
                it[userId] = user.id
                it[taskId] = task?.id
                it[description] = change.description
                it[reason] = change.reason
                it[income] = change.income
                it[outcome] = change.outcome
                it[createdAt] = now
            }

            BalanceChanged(payload = change, timestamp = now)
        }
    }
}
