package ru.upg.ates.analytic.model

import org.jetbrains.exposed.sql.ResultRow
import ru.upg.ates.analytic.table.TaskTable
import java.util.*

data class Task(
    val id: Long,
    val pid: UUID,
    val name: String,
    val finished: Boolean,
    val assignedToId: Long
) {
    constructor(row: ResultRow) : this(
        id = row[TaskTable.id].value,
        pid = row[TaskTable.pid],
        name = row[TaskTable.title],
        finished = row[TaskTable.finished],
        assignedToId = row[TaskTable.assignedToId]
    )
}
