package ru.upg.ates.billing.query

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.billing.BillingDomain
import ru.upg.ates.billing.model.Task
import java.util.UUID

class GetTaskQuery(private val taskPid: UUID): Query<BillingDomain, Task> {
    override fun execute(domain: BillingDomain): Task {
        val (users, tasks) = domain.tables
        return transaction {
            tasks.leftJoin(users)
                .selectAll()
                .andWhere { tasks.pid eq taskPid }
                .firstOrNull()
                ?.let { Task(tasks, users, it) }
                ?: throw IllegalStateException(
                    "Task not found $taskPid"
                )
        }
    }
}
