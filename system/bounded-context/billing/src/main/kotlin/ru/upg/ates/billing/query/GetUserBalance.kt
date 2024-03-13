package ru.upg.ates.billing.query

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.table.BalanceChangeTable
import ru.upg.ates.billing.table.UserTable
import java.util.UUID

class GetUserBalance(private val userPid: UUID) : Query<BillingContext, Long> {
    override fun execute(context: BillingContext): Long {
        return transaction { 
            BalanceChangeTable
                .leftJoin(UserTable)
                .select(BalanceChangeTable.balance)
                .andWhere { UserTable.pid eq userPid }
                .groupBy(BalanceChangeTable.userId)
                .firstOrNull()
                ?.let { it[BalanceChangeTable.balance] }
                ?: 0L
        }
    }
}
