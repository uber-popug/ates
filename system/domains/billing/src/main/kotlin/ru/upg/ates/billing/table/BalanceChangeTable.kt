package ru.upg.ates.billing.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import ru.upg.ates.events.BalanceChangeType

object BalanceChangeTable : LongIdTable("balance_change") {
    val userId = long("user_id").references(UserTable.id)
    val taskId = long("task_id").references(TaskTable.id)
    val type = enumeration<BalanceChangeType>("type")
    val description = text("description")
    val income = integer("income")
    val outcome = integer("outcome")
    val timestamp = timestamp("timestamp")
}
