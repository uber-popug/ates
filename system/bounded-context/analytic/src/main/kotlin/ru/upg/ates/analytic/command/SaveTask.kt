package ru.upg.ates.analytic.command

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import ru.upg.ates.Command
import ru.upg.ates.analytic.AnalyticContext
import ru.upg.ates.analytic.query.GetUser
import ru.upg.ates.analytic.table.TaskTable
import ru.upg.ates.events.TaskCreatedV1
import ru.upg.ates.execute

class SaveTask(
    private val event: TaskCreatedV1
) : Command.Silent<AnalyticContext> {

    override fun execute(context: AnalyticContext) {
        transaction {
            val user = context.execute(GetUser(event.assignedToPid))
            TaskTable.upsert(keys = arrayOf(TaskTable.pid)) {
                it[pid] = event.pid
                it[assignedToId] = user.id
                it[title] = event.title
                it[finished] = event.finished
            }
        }
    }
}
