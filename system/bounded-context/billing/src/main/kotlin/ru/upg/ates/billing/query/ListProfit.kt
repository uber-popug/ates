package ru.upg.ates.billing.query

import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.day
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.model.DayProfit
import ru.upg.ates.billing.table.BalanceChangeTable
import ru.upg.ates.events.BalanceChangeReason
import java.time.LocalDate

class ListProfit(
    private val from: LocalDate,
    private val to: LocalDate
) : Query<BillingContext, List<DayProfit>> {
    
    override fun execute(context: BillingContext): List<DayProfit> {
        return transaction {
            val reasons = listOf(BalanceChangeReason.TASK_ASSIGNED, BalanceChangeReason.TASK_FINISHED)
            val totalIncome = BalanceChangeTable.income.sum()
            val totalOutcome = BalanceChangeTable.outcome.sum()
            val profit = totalOutcome - totalIncome
            val day = BalanceChangeTable.createdAt.date() 
            BalanceChangeTable
                .select(day, profit)
                .andWhere { day.between(from, to) }
                .andWhere { BalanceChangeTable.reason inList reasons }
                .groupBy(day)
                .map { DayProfit(it[day], it[profit] ?: 0) }
        }
    }
}
