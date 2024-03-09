package ru.upg.ates.events

import ru.upg.ates.Event
import java.time.Instant
import java.util.*

enum class BalanceChangeReason(val description: String) {
    TASK_ASSIGNED("Назначена задача"),
    TASK_FINISHED("Выполнена задача"),
    PAYMENT_SENT("Направлена выплата")
}

data class BalanceChanged(
    val userPid: UUID,
    val taskPid: UUID?,
    val description: String,
    val reason: BalanceChangeReason,
    val income: Long,
    val outcome: Long,
    override val id: UUID = UUID.randomUUID(),
    override val timestamp: Instant = Instant.now()
) : Event {
    override val jsonSchemaId = "#/billing/balance/changed/1"
    override val name = "BalanceChanged"
    override val version = 1
    override val producer = "billing"
}


enum class PaymentStatus {
    CREATED
}

data class PaymentCreated(
    val targetUserPid: UUID,
    val description: String,
    val amount: Long,
    override val id: UUID = UUID.randomUUID(),
    override val timestamp: Instant = Instant.now()
) : Event {
    override val jsonSchemaId = "#/billing/payment/created/1"
    override val name = "PaymentCreated"
    override val version = 1
    override val producer = "billing"
}
