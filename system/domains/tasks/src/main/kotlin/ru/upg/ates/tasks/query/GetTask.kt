package ru.upg.ates.tasks.query

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.model.Task
import ru.upg.ates.common.cqrs.Query

class GetTask(private val id: Long) : Query<TasksDomain, Task>() {
    override fun execute(domain: TasksDomain): Task {
        return transaction {
            domain.tables.tasks.let { table ->
                table.selectAll()
                    .where { table.id eq this@GetTask.id }
                    .firstOrNull()
                    ?.let { row: ResultRow -> Task(table, row) }
                    ?: throw IllegalStateException(
                        "Task ${this@GetTask.id} not found"
                    )
            }
        }
    }
}