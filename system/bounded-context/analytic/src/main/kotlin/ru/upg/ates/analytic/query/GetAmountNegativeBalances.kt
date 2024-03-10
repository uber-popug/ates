package ru.upg.ates.analytic.query

import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.analytic.AnalyticContext
import ru.upg.ates.analytic.table.BalanceChangeTable
import java.time.LocalDate
import java.time.ZoneOffset

object GetAmountNegativeBalances : Query<AnalyticContext, Long> {
    override fun execute(context: AnalyticContext): Long {
        return transaction {
            val startDay = LocalDate.now().atStartOfDay(ZoneOffset.systemDefault()).toInstant()
            val endDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneOffset.systemDefault()).toInstant()
            val totalIncome = BalanceChangeTable.income.sum()
            val totalOutcome = BalanceChangeTable.outcome.sum()
            val balance = totalIncome - totalOutcome
            BalanceChangeTable.select(balance)
                .andWhere { BalanceChangeTable.createdAt.between(startDay, endDay) }
                .groupBy(BalanceChangeTable.userId)
                .having { balance greater 0L }
                .count()
        }
    }
}
