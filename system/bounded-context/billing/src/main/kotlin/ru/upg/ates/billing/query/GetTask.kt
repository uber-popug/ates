package ru.upg.ates.billing.query

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.model.Task
import ru.upg.ates.billing.table.TaskTable
import ru.upg.ates.billing.table.UserTable
import java.util.*

class GetTask(private val taskPid: UUID): Query<BillingContext, Task> {
    override fun execute(context: BillingContext): Task {
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
