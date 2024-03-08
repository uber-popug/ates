package ru.upg.ates.events

import java.time.Instant
import java.util.UUID

enum class BalanceChangeType {
    TASK_ASSIGNED,
    TASK_FINISHED,
    PAYMENT_SENT
}

data class BalanceChangedEvent(
    override val payload: Payload,
    override val id: UUID = UUID.randomUUID(),
    override val timestamp: Instant = Instant.now(),
) : BusinessEvent<BalanceChangedEvent.Payload> {

    data class Payload(
        val userPid: UUID,
        val taskPid: UUID,
        val income: Int,
        val outcome: Int,
    )

    override val jsonSchemaId = "#/billing/balance/changed/1"
    override val name = "BalanceChanged"
    override val version = 1
    override val producer = "billing"
}
