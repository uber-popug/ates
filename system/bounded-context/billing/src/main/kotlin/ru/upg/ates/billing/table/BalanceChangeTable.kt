package ru.upg.ates.billing.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.sum
import ru.upg.ates.events.BalanceChangeReason

object BalanceChangeTable : LongIdTable("balance_change") {
    val userId = long("user_id").references(UserTable.id)
    val taskId = long("task_id").references(TaskTable.id).nullable()
    val reason = enumeration<BalanceChangeReason>("reason")
    val description = text("description")
    val income = long("income")
    val outcome = long("outcome")
    val createdAt = timestamp("created_at")

    val totalIncome = income.sum()
    val totalOutcome = outcome.sum()
    val balance = totalIncome - totalOutcome
    val profit = totalOutcome - totalIncome
}
