package ru.upg.ates.tasks.model

import org.jetbrains.exposed.sql.ResultRow
import ru.upg.ates.events.Role
import ru.upg.ates.tasks.table.UserTable
import java.util.*

data class User(
    val id: Long,
    val pid: UUID,
    val role: Role,
    val username: String
) {
    constructor(table: UserTable, row: ResultRow) : this(
        id = row[table.id].value,
        pid = row[table.pid],
        role = row[table.role],
        username = row[table.username]
    )
}
