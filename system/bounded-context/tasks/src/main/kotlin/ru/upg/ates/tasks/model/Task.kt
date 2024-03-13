package ru.upg.ates.tasks.model

import org.jetbrains.exposed.sql.ResultRow
import ru.upg.ates.tasks.table.TaskTable
import ru.upg.ates.tasks.table.UserTable
import java.util.*

data class Task(
    val id: Long,
    val pid: UUID,
    val title: String,
    val jiraId: Long,
    val finished: Boolean,
    val assignedTo: User,
) {
    constructor(row: ResultRow) : this(
        id = row[TaskTable.id].value,
        pid = row[TaskTable.pid],
        title = row[TaskTable.title],
        jiraId = row[TaskTable.jiraId],
        finished = row[TaskTable.finished],
        assignedTo = User(row)
    )
}
