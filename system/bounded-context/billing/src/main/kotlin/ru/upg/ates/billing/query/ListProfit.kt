package ru.upg.ates.billing.query

import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.day
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.model.DayProfit
import ru.upg.ates.billing.table.BalanceChangeTable
import java.time.LocalDate

class ListProfit(
    private val from: LocalDate,
    private val to: LocalDate
) : Query<BillingContext, List<DayProfit>> {
    
    override fun execute(context: BillingContext): List<DayProfit> {
        transaction { 
            val day = BalanceChangeTable.createdAt.date() 
            BalanceChangeTable.select(day)
        }
    }
}
