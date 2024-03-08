package ru.upg.ates.billing.command

import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.query.GetTask
import ru.upg.ates.events.BalanceChanged
import ru.upg.ates.events.TaskAssigned
import ru.upg.ates.execute

class ProcessTaskAssigned(
    private val event: TaskAssigned
): Command.Silent<BillingContext> {

    override fun execute(context: BillingContext) {
        transaction {
            val task = context.execute(GetTask(event.payload.taskPid))

            val change = BalanceChanged.Payload(
                userPid = event.payload.assignedTo,
                taskPid = event.payload.taskPid,
                income = 0,
                outcome = task.assignPrice
            )

            context.publish(
                event = context.execute(
                    command = SaveBalanceChange(task, change)
                )
            )
        }
    }
}
