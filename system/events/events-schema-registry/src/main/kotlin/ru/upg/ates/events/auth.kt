package ru.upg.ates.events

import java.time.LocalDateTime
import java.util.*


enum class Role(val label: String) {
    ADMIN("Администратора"),
    MANAGER("Менеджер"),
    WORKER("Работник"),
}

data class UserChange(
    val pid: UUID,
    val role: Role,
    val username: String,
)

sealed class UserCUD(
    override val id: UUID = UUID.randomUUID(),
    override val timestamp: LocalDateTime = LocalDateTime.now(),
) : CUDEvent<UserChange> {

    data class Created(override val payload: UserChange) : UserCUD() {
        override val jsonSchemaId = "#/users/created/1.yaml"
        override val name = "UserCreated"
        override val version = 1
        override val producer = "tasks"
    }

    data class Updated(override val payload: UserChange) : UserCUD() {
        override val jsonSchemaId = "#/users/updated/1.yaml"
        override val name = "UserUpdated"
        override val version = 1
        override val producer = "tasks"
    }
}
