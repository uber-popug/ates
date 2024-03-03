package ru.upg.ates.event

import ru.upg.ates.AtesEvent
import ru.upg.ates.CUDEvent
import java.util.UUID


enum class Role(val label: String) {
    ADMIN("Администратора"),
    MANAGER("Менеджер"),
    WORKER("Работник"),
}

data class User(
    val id: Long,
    val pid: UUID,
    val username: String,
    val role: Role
)


interface UserCUD : AtesEvent, CUDEvent {
    data class Created(val user: User) : UserCUD
    data class Updated(val user: User) : UserCUD
}

