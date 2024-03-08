package ru.upg.ates.billing.command

import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.query.GetTask
import ru.upg.ates.events.BalanceChanged
import ru.upg.ates.events.TaskFinished
import ru.upg.ates.execute

class ProcessTaskFinished(
    private val event: TaskFinished
): Command.Silent<BillingContext> {

    override fun execute(context: BillingContext) {
        transaction {
            val task = context.execute(GetTask(event.payload.taskPid))

            val change = BalanceChanged.Payload(
                userPid = event.payload.finishedBy,
                taskPid = event.payload.taskPid,
                income = task.finishPrice,
                outcome = 0
            )

            context.publish(
                event = context.execute(
                    command = SaveBalanceChange(task, change)
                )
            )
        }
    }
}
