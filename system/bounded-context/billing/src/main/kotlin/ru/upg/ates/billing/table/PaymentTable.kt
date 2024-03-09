package ru.upg.ates.billing.table

import org.jetbrains.exposed.sql.javatime.timestamp
import ru.upg.ates.events.PaymentStatus
import ru.upg.ates.table.AtesTable

object PaymentTable : AtesTable("payment") {
    val userId = long("user_id").references(UserTable.id)
    val description = text("description")
    val amount = long("amount")
    val status = enumeration<PaymentStatus>("status")
    val createdAt = timestamp("created_at")
}
