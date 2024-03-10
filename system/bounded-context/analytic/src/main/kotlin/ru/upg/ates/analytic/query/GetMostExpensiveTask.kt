package ru.upg.ates.analytic.query

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.analytic.AnalyticContext
import ru.upg.ates.analytic.model.Task
import ru.upg.ates.analytic.table.BalanceChangeTable
import ru.upg.ates.analytic.table.TaskTable
import ru.upg.ates.analytic.table.UserTable
import ru.upg.ates.events.BalanceChangeReason
import java.time.LocalDate
import java.time.ZoneOffset

class GetMostExpensiveTask(
    private val from: LocalDate,
    private val to: LocalDate
): Command<AnalyticContext, Task?> {

    override fun execute(context: AnalyticContext): Task? {
        return transaction {
            val fromInstant = from.atStartOfDay().toInstant(ZoneOffset.UTC)
            val toInstant = to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)
            TaskTable.leftJoin(BalanceChangeTable)
                .select(TaskTable.columns + BalanceChangeTable.income.max())
                .andWhere { BalanceChangeTable.taskId.isNotNull() }
                .andWhere { BalanceChangeTable.reason eq BalanceChangeReason.TASK_FINISHED }
                .andWhere { BalanceChangeTable.createdAt.between(fromInstant, toInstant) }
                .groupBy(TaskTable.id)
                .firstOrNull()
                ?.let { Task(it) }
        }
    }
}
