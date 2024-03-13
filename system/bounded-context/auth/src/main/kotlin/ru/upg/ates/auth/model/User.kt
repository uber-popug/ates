package ru.upg.ates.auth.model

import ru.upg.ates.events.Role
import ru.upg.ates.events.UserCreated
import java.util.*

data class User(
    val id: Long,
    val pid: UUID,
    val role: Role,
    val username: String
) {
    constructor(id: Long, change: UserCreated) : this(
        id = id,
        pid = change.pid,
        role = change.role,
        username = change.username
    )
}
