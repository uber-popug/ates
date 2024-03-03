package ru.upg.ates.tasks.table

import org.jetbrains.exposed.dao.id.UUIDTable
import ru.upg.ates.event.Role

object UserTable : UUIDTable("user", "pid") {
    val role = enumeration<Role>("role")
}