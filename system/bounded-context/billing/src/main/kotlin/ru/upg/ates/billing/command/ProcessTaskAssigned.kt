package ru.upg.ates.billing.command

import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.query.GetTask
import ru.upg.ates.billing.query.GetUser
import ru.upg.ates.events.BalanceChangeReason
import ru.upg.ates.events.BalanceChanged
import ru.upg.ates.events.TaskAssigned
import ru.upg.ates.execute

class ProcessTaskAssigned(
    private val event: TaskAssigned,
) : Command.Silent<BillingContext> {

    private val reason = BalanceChangeReason.TASK_ASSIGNED
    
    override fun execute(context: BillingContext) = with(context) {
        transaction {
            val task = execute(GetTask(event.taskPid))
            val user = execute(GetUser(event.assignedTo))

            val change = BalanceChanged(
                userPid = event.assignedTo,
                taskPid = event.taskPid,
                reason = reason,
                description = reason.description,
                income = 0,
                outcome = task.assignPrice
            )

            execute(SaveBalanceChange(user, task, change))
        }
    }
}
