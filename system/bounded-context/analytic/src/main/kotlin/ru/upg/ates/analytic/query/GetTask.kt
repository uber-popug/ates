package ru.upg.ates.analytic.query

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.analytic.AnalyticContext
import ru.upg.ates.analytic.model.Task
import ru.upg.ates.analytic.table.TaskTable
import ru.upg.ates.analytic.table.UserTable
import java.util.*

class GetTask(private val taskPid: UUID): Query<AnalyticContext, Task> {
    override fun execute(context: AnalyticContext): Task {
        val (tasks, users) = TaskTable to UserTable
        return transaction {
            tasks.leftJoin(users)
                .selectAll()
                .andWhere { tasks.pid eq taskPid }
                .firstOrNull()
                ?.let { Task(it) }
                ?: throw IllegalStateException("Task not found $taskPid")
        }
    }
}
