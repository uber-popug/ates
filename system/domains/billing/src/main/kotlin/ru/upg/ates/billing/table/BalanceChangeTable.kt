package ru.upg.ates.billing.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import ru.upg.ates.events.BalanceChangeReason

object BalanceChangeTable : LongIdTable("balance_change") {
    val userId = long("user_id").references(UserTable.id)
    val taskId = long("task_id").references(TaskTable.id).nullable()
    val reason = enumeration<BalanceChangeReason>("reason")
    val description = text("description")
    val income = long("income")
    val outcome = long("outcome")
    val createdAt = timestamp("created_at")
}
