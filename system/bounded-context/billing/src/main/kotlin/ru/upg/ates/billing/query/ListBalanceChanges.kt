package ru.upg.ates.billing.query

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.model.BalanceChange
import ru.upg.ates.billing.table.BalanceChangeTable
import ru.upg.ates.billing.table.TaskTable
import ru.upg.ates.billing.table.UserTable
import java.util.UUID

class ListBalanceChanges(
    private val userPid: UUID
) : Query<BillingContext, List<BalanceChange>> {
    
    override fun execute(context: BillingContext): List<BalanceChange> {
        return transaction { 
            BalanceChangeTable
                .leftJoin(TaskTable.leftJoin(UserTable))
                .select(BalanceChangeTable.columns + TaskTable.columns + UserTable.columns)
                .andWhere { UserTable.pid eq userPid }
                .map { BalanceChange(it) }
        }
    }
}
