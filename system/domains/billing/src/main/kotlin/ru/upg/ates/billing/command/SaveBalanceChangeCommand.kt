package ru.upg.ates.billing.command

import org.jetbrains.exposed.sql.insert
import ru.upg.ates.Command
import ru.upg.ates.billing.BillingDomain
import ru.upg.ates.billing.model.Task
import ru.upg.ates.events.BalanceChangeType
import ru.upg.ates.events.BalanceChangedEvent
import ru.upg.ates.events.Event
import java.time.Instant

class SaveBalanceChangeCommand(
    private val task: Task,
    private val change: BalanceChangedEvent.Payload
): Command<BillingDomain, BalanceChangedEvent> {

    override fun execute(domain: BillingDomain): Pair<BalanceChangedEvent, List<Event<*>>> {
        val now = Instant.now()

        domain.tables.balanceChanges.insert {
            it[userId] = task.assignedTo.id
            it[taskId] = task.id
            it[type] = BalanceChangeType.TASK_FINISHED
            it[description] = "Задача выполнена"
            it[income] = task.finishPrice
            it[outcome] = 0
            it[timestamp] = now
        }

        return BalanceChangedEvent(payload = change, timestamp = now) to listOf()
    }
}
