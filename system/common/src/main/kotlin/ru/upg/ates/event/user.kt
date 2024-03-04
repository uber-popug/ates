package ru.upg.ates.event

import ru.upg.ates.AtesEvent
import ru.upg.ates.CUDEvent
import ru.upg.ates.common.cqrs.IAggregate
import java.util.UUID


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


interface UserCUD : AtesEvent, CUDEvent, IAggregate {
    data class Created(val user: UserChange) : UserCUD
    data class Updated(val user: UserChange) : UserCUD
}
