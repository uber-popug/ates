package ru.upg.ates.analytic.model

import org.jetbrains.exposed.sql.ResultRow
import ru.upg.ates.analytic.table.TaskTable
import ru.upg.ates.analytic.table.UserTable
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
        name = row[tasks.title],
        assignPrice = row[tasks.assignPrice],
        finishPrice = row[tasks.finishPrice],
        finished = row[tasks.finished],
        assignedTo = User(users, row)
    )
}
