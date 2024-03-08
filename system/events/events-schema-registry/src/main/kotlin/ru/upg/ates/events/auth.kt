package ru.upg.ates.events

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

sealed class UserChanged : Event<UserChange>() {
    data class Created(override val payload: UserChange) : UserChanged() {
        override val jsonSchemaId = "#/users/created/1.yaml"
        override val name = "UserCreated"
        override val version = 1
        override val producer = "tasks"
    }

    data class Updated(override val payload: UserChange) : UserChanged() {
        override val jsonSchemaId = "#/users/updated/1.yaml"
        override val name = "UserUpdated"
        override val version = 1
        override val producer = "tasks"
    }
}
