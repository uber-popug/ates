package ru.upg.ates.billing.query

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.model.BalanceChange
import ru.upg.ates.billing.table.BalanceChangeTable
import ru.upg.ates.billing.table.TaskTable
import ru.upg.ates.billing.table.UserTable

class ListBalanceChanges(
    private val userId: Long
) : Query<BillingContext, List<BalanceChange>> {
    
    override fun execute(context: BillingContext): List<BalanceChange> {
        return transaction { 
            BalanceChangeTable
                .leftJoin(TaskTable.leftJoin(UserTable))
                .select(BalanceChangeTable.columns + TaskTable.columns)
                .andWhere { BalanceChangeTable.userId eq userId }
                .map { BalanceChange(it) }
        }
    }
}
