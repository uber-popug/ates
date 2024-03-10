package ru.upg.ates.billing.command

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import ru.upg.ates.Command
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.query.GetUser
import ru.upg.ates.billing.table.TaskTable
import ru.upg.ates.events.TaskCreated
import ru.upg.ates.execute

class SaveTask(
    private val event: TaskCreated
) : Command.Silent<BillingContext> {

    override fun execute(context: BillingContext) {
        transaction {
            val user = context.execute(GetUser(event.assignedToPid))
            
            val taskPrice = (1..20L).random()
            TaskTable.upsert(keys = arrayOf(TaskTable.pid)) {
                it[pid] = event.pid
                it[assignedToId] = user.id
                it[title] = event.title
                it[assignPrice] = taskPrice
                it[finishPrice] = taskPrice * 2
                it[finished] = event.finished
            }
        }
    }
}
