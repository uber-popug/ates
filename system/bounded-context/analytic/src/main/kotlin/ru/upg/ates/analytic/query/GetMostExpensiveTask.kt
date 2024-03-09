package ru.upg.ates.analytic.query

import org.jetbrains.exposed.sql.SqlExpressionBuilder.between
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.analytic.AnalyticContext
import ru.upg.ates.analytic.model.Task
import ru.upg.ates.analytic.table.BalanceChangeTable
import ru.upg.ates.analytic.table.TaskTable
import ru.upg.ates.analytic.table.UserTable
import ru.upg.ates.events.BalanceChangeReason
import java.time.Instant

class GetMostExpensiveTask(
    private val from: Instant,
    private val to: Instant
): Command<AnalyticContext, Task?> {

    override fun execute(context: AnalyticContext): Task? {
        return transaction {
            BalanceChangeTable
                .leftJoin(TaskTable.leftJoin(UserTable))
                .select(TaskTable.columns + BalanceChangeTable.income.max())
                .andWhere { BalanceChangeTable.taskId.isNotNull() }
                .andWhere { BalanceChangeTable.reason eq BalanceChangeReason.TASK_FINISHED }
                .andWhere { BalanceChangeTable.createdAt.between(from, to) }
                .groupBy(BalanceChangeTable.taskId)
                .firstOrNull()
                ?.let { Task(TaskTable, UserTable, it)}
        }
    }
}
