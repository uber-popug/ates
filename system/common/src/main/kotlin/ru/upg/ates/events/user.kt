package ru.upg.ates.events

import ru.upg.ates.CUDEvent
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

sealed interface UserCUD : CUDEvent {
    data class Created(val user: UserChange) : UserCUD
    data class Updated(val user: UserChange) : UserCUD
}
