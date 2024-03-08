package ru.upg.ates.events

import ru.upg.ates.Event
import java.util.*

enum class Role(val label: String) {
    ADMIN("Администратора"),
    MANAGER("Менеджер"),
    WORKER("Работник"),
}

data class UserCreated(
    val pid: UUID,
    val role: Role,
    val username: String
) : Event {
    override val jsonSchemaId = "#/users/created/1.yaml"
    override val name = "UserCreated"
    override val version = 1
    override val producer = "tasks"
}

