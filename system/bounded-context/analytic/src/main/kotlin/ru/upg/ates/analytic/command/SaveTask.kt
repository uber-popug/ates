package ru.upg.ates.analytic.command

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import ru.upg.ates.Command
import ru.upg.ates.analytic.AnalyticContext
import ru.upg.ates.analytic.table.TaskTable
import ru.upg.ates.events.TaskCreated

class SaveTask(
    private val event: TaskCreated
) : Command.Silent<AnalyticContext> {

    override fun execute(context: AnalyticContext) {
        transaction {
            TaskTable.upsert(keys = arrayOf(TaskTable.pid)) {
                it[pid] = event.pid
                it[title] = event.title
                it[finished] = event.finished
            }
        }
    }
}
