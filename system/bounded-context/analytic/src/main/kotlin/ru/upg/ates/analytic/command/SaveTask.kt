package ru.upg.ates.analytic.command

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import ru.upg.ates.Command
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.table.TaskTable
import ru.upg.ates.events.TaskCreated

class SaveTask(
    private val event: TaskCreated
) : Command.Silent<BillingContext> {

    override fun execute(context: BillingContext) {
        transaction {
            val taskPrice = (1..20L).random()
            TaskTable.upsert(keys = arrayOf(TaskTable.pid)) {
                it[pid] = event.pid
                it[name] = event.name
                it[assignPrice] = assignPrice
                it[finishPrice] = taskPrice * 2
                it[finished] = event.finished
            }
        }
    }
}
