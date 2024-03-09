package ru.upg.ates.events

import ru.upg.ates.Event
import java.time.Instant
import java.util.*

enum class BalanceChangeReason(val description: String) {
    TASK_ASSIGNED("Назначена задача"),
    TASK_FINISHED("Выполнена задача"),
    PAYMENT_SENT("Направлена выплата")
}

@Event("#/billing/balance/changed/1.yaml", "BalanceChanged", 1)
data class BalanceChanged(
    val userPid: UUID,
    val taskPid: UUID?,
    val description: String,
    val reason: BalanceChangeReason,
    val income: Long,
    val outcome: Long
)


enum class PaymentStatus {
    CREATED
}

@Event("#/billing/payment/created/1.yaml", "PaymentCreated", 1)
data class PaymentCreated(
    val targetUserPid: UUID,
    val description: String,
    val amount: Long
)
