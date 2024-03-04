package ru.upg.ates.auth.table

import org.jetbrains.exposed.dao.id.LongIdTable
import ru.upg.ates.event.Role

object UserTable : LongIdTable("user") {
    val pid = uuid("pid")
    val role = enumeration<Role>("role")
    val username = text("username")
    val password = text("password")
}