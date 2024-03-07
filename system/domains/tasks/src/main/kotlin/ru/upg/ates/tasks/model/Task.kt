package ru.upg.ates.tasks.model

import org.jetbrains.exposed.sql.ResultRow
import ru.upg.ates.tasks.table.TaskTable
import ru.upg.ates.tasks.table.UserTable
import java.util.*

data class Task(
    val id: Long,
    val pid: UUID,
    val name: String,
    val price: Int,
    val finished: Boolean,
    val user: User,
) {
    constructor(
        tasks: TaskTable,
        users: UserTable,
        row: ResultRow
    ) : this(
        id = row[tasks.id].value,
        pid = row[tasks.pid],
        name = row[tasks.name],
        price = row[tasks.price],
        finished = row[tasks.finished],
        user = User(users, row)
    )
}
