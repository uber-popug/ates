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
    constructor(row: ResultRow) : this(
        id = row[UserTable.id].value,
        pid = row[UserTable.pid],
        role = row[UserTable.role],
        username = row[UserTable.username]
    )
}
