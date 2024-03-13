package ru.upg.ates.billing.model

import org.jetbrains.exposed.sql.ResultRow
import ru.upg.ates.billing.table.BalanceChangeTable
import ru.upg.ates.events.BalanceChangeReason
import java.time.Instant

data class BalanceChange(
    val id: Long,
    val reason: BalanceChangeReason,
    val description: String,
    val timestamp: Instant,
    val income: Long,
    val outcome: Long,
    val task: Task?
) {
    constructor(row: ResultRow) : this(
        id = row[BalanceChangeTable.id].value,
        reason = row[BalanceChangeTable.reason],
        description = row[BalanceChangeTable.description],
        timestamp = row[BalanceChangeTable.createdAt],
        income = row[BalanceChangeTable.income],
        outcome = row[BalanceChangeTable.outcome],
        task = Task(row)
    )
}
