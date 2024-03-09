package ru.upg.ates.tasks.model

import org.jetbrains.exposed.sql.ResultRow
import ru.upg.ates.tasks.table.TaskTable
import ru.upg.ates.tasks.table.UserTable
import java.util.*

data class Task(
    val id: Long,
    val pid: UUID,
    val title: String,
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
        title = row[tasks.title],
        finished = row[tasks.finished],
        assignedTo = User(users, row)
    )
}
