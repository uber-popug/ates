package ru.upg.ates.tasks.query

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.tasks.TasksContext
import ru.upg.ates.tasks.model.Task
import ru.upg.ates.tasks.table.TaskTable
import ru.upg.ates.tasks.table.UserTable

class GetTask(private val taskId: Long) : Query<TasksContext, Task> {
    override fun execute(context: TasksContext): Task {
        val (tasks, users) = TaskTable to UserTable
        return transaction {
            tasks.leftJoin(users)
                .selectAll()
                .andWhere { tasks.id eq taskId }
                .firstOrNull()
                ?.let { Task(tasks, users, it) }
                ?: throw IllegalStateException(
                    "Task $taskId not found"
                )
        }
    }
}
