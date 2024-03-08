package ru.upg.ates.billing.command

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import ru.upg.ates.Command
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.table.TaskTable
import ru.upg.ates.events.TaskChanged

class SaveTask(
    private val event: TaskChanged,
) : Command.Silent<BillingContext> {

    override fun execute(context: BillingContext) {
        when (event) {
            is TaskChanged.Created -> create(event.payload)
            is TaskChanged.Updated -> update(event.payload)
        }
    }

    private fun create(change: TaskChanged.Payload) {
        transaction {
            val taskPrice = (1..20).random()
            TaskTable.upsert(keys = arrayOf(TaskTable.pid)) {
                it[pid] = change.pid
                it[name] = change.name
                it[assignPrice] = assignPrice
                it[finishPrice] = taskPrice * 2
                it[finished] = change.finished
            }
        }
    }

    private fun update(change: TaskChanged.Payload) {
        transaction {
            TaskTable.update({ TaskTable.pid eq change.pid }) {
                it[pid] = change.pid
                it[name] = change.name
            }
        }
    }
}
