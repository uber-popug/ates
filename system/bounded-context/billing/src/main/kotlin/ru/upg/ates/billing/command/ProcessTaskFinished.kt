package ru.upg.ates.billing.command

import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.query.GetTask
import ru.upg.ates.billing.query.GetUser
import ru.upg.ates.events.BalanceChangeReason
import ru.upg.ates.events.BalanceChanged
import ru.upg.ates.events.TaskFinished
import ru.upg.ates.execute

class ProcessTaskFinished(
    private val event: TaskFinished,
) : Command.Silent<BillingContext> {

    private val reason = BalanceChangeReason.TASK_ASSIGNED

    override fun execute(context: BillingContext) = with(context) {
        transaction {
            val task = execute(GetTask(event.taskPid))
            val user = execute(GetUser(event.finishedBy))

            val change = BalanceChanged(
                userPid = event.finishedBy,
                taskPid = event.taskPid,
                reason = reason,
                description = reason.description,
                income = task.finishPrice,
                outcome = 0
            )

            execute(SaveBalanceChange(user, task, change))
        }
    }
}
