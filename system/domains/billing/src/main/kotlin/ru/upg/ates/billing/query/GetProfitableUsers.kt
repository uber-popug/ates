package ru.upg.ates.billing.query

import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.model.User
import ru.upg.ates.billing.table.BalanceChangeTable
import ru.upg.ates.billing.table.UserTable

object GetProfitableUsers : Query<BillingContext, List<Pair<User, Long>>> {
    override fun execute(context: BillingContext): List<Pair<User, Long>> {
        return transaction {
            val totalIncome = BalanceChangeTable.income.sum()
            val totalOutcome = BalanceChangeTable.outcome.sum()
            val balance = totalIncome - totalOutcome
            val columns = UserTable.columns + listOf(BalanceChangeTable.userId, balance)
            BalanceChangeTable
                .leftJoin(UserTable)
                .select(columns)
                .andWhere { balance greater 0L }
                .groupBy(BalanceChangeTable.userId)
                .map { User(UserTable, it) to (it[balance] ?: 0L) }
                .filter { it.second > 0 }
        }
    }
}
