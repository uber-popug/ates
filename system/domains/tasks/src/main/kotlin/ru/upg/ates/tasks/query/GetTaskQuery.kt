package ru.upg.ates.tasks.query

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.model.Task

class GetTaskQuery(private val id: Long) : Query<TasksDomain, Task> {
    override fun execute(context: TasksDomain): Task {
        val (tasks, users) = context.tables
        return transaction {
            tasks.leftJoin(users)
                .selectAll()
                .andWhere { tasks.id eq this@GetTaskQuery.id }
                .firstOrNull()
                ?.let { Task(tasks, users, it) }
                ?: throw IllegalStateException(
                    "Task ${this@GetTaskQuery.id} not found"
                )
        }
    }
}
