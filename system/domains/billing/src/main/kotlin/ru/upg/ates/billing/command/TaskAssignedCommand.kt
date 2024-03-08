package ru.upg.ates.billing.command

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.billing.BillingDomain
import ru.upg.ates.billing.query.GetTaskQuery
import ru.upg.ates.events.BalanceChangeType
import ru.upg.ates.events.BalanceChangedEvent
import ru.upg.ates.events.Event
import ru.upg.ates.events.TaskBE
import ru.upg.ates.fetch

class TaskAssignedCommand(
    private val event: TaskBE.Assigned
): Command<BillingDomain, Unit> {

    override fun execute(domain: BillingDomain): Pair<Unit, List<Event<*>>> {
        val balanceChangedEvent = transaction {
            val task = domain.fetch(GetTaskQuery(event.payload.taskPid))

            val event = BalanceChangedEvent(
                payload = BalanceChangedEvent.Payload(
                    userPid = event.payload.assignedTo,
                    taskPid = event.payload.taskPid,
                    income = 0,
                    outcome = task.assignPrice
                )
            )

            val balanceChanges = domain.tables.balanceChanges
            balanceChanges.insert {
                it[long("user_id").references(id)] = task.assignedTo.id
                it[taskId] = task.id
                it[type] = BalanceChangeType.TASK_FINISHED
                it[description] = "Задача выполнена"
                it[income] = task.finishPrice
                it[outcome] = 0
                it[timestamp] = event.timestamp
            }

            event
        }

        return Unit to listOf(balanceChangedEvent)
    }
}
