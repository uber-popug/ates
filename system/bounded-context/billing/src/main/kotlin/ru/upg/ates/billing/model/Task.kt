package ru.upg.ates.billing.model

import org.jetbrains.exposed.sql.ResultRow
import ru.upg.ates.billing.table.TaskTable
import ru.upg.ates.billing.table.UserTable
import java.util.*

data class Task(
    val id: Long,
    val pid: UUID,
    val name: String,
    val assignPrice: Long,
    val finishPrice: Long,
    val finished: Boolean,
    val assignedTo: User,
) {
    constructor(
        tasks: TaskTable,
        users: UserTable,
        row: ResultRow
    ) : this(
        id = row[tasks.id].value,
        pid = row[tasks.pid],
        name = row[tasks.name],
        assignPrice = row[tasks.assignPrice],
        finishPrice = row[tasks.finishPrice],
        finished = row[tasks.finished],
        assignedTo = User(users, row)
    )
}
