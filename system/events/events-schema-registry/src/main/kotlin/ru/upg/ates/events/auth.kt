package ru.upg.ates.events

import ru.upg.ates.Event
import java.time.Instant
import java.util.*

enum class Role(val label: String) {
    ADMIN("Администратора"),
    MANAGER("Менеджер"),
    WORKER("Работник"),
}

@Event("#/users/created/1.yaml", "UserCreated", 1)
data class UserCreated(
    val pid: UUID,
    val role: Role,
    val username: String
)

