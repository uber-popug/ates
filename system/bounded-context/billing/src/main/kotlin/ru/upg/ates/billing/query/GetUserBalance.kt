package ru.upg.ates.billing.query

import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.table.BalanceChangeTable

class GetUserBalance(private val userId: Long) : Query<BillingContext, Long> {
    override fun execute(context: BillingContext): Long {
        return transaction { 
            BalanceChangeTable
                .select(BalanceChangeTable.balance)
                .groupBy(BalanceChangeTable.userId)
                .firstOrNull()
                ?.let { it[BalanceChangeTable.balance] }
                ?: 0L
        }
    }
}
