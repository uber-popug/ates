package ru.upg.ates.billing.model

import org.jetbrains.exposed.sql.ResultRow
import ru.upg.ates.billing.table.TaskTable
import ru.upg.ates.billing.table.UserTable
import java.util.*

data class Task(
    val id: Long,
    val pid: UUID,
    val title: String,
    val assignPrice: Long,
    val finishPrice: Long,
    val finished: Boolean,
    val assignedTo: User,
) {
    constructor(
        users: UserTable,
        row: ResultRow
    ) : this(
        id = row[TaskTable.id].value,
        pid = row[TaskTable.pid],
        title = row[TaskTable.title],
        assignPrice = row[TaskTable.assignPrice],
        finishPrice = row[TaskTable.finishPrice],
        finished = row[TaskTable.finished],
        assignedTo = User(UserTable, row)
    )
}
