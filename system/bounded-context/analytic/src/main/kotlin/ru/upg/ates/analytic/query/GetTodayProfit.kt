package ru.upg.ates.analytic.query

import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.analytic.AnalyticContext
import ru.upg.ates.analytic.table.BalanceChangeTable
import ru.upg.ates.events.BalanceChangeReason
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset

object GetTodayProfit : Query<AnalyticContext, Long> {
    override fun execute(context: AnalyticContext): Long {
        return transaction { 
            addLogger(StdOutSqlLogger)
            
            val reasons = listOf(BalanceChangeReason.TASK_ASSIGNED, BalanceChangeReason.TASK_FINISHED)
            val startDay = LocalDate.now().atStartOfDay(ZoneOffset.systemDefault()).toInstant()
            val endDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneOffset.systemDefault()).toInstant()
            val totalIncome = BalanceChangeTable.income.sum()
            val totalOutcome = BalanceChangeTable.outcome.sum()
            val profit = totalOutcome - totalIncome
            BalanceChangeTable.select(profit)
                .andWhere { BalanceChangeTable.createdAt.between(startDay, endDay) }
                .andWhere { BalanceChangeTable.reason inList reasons }
                .firstOrNull()
                ?.let { it[profit] }
                ?: 0L
        }
    }
}
