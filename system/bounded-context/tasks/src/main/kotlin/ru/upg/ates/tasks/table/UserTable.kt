package ru.upg.ates.tasks.table

import ru.upg.ates.events.Role
import ru.upg.ates.table.AtesTable

object UserTable : AtesTable("user") {
    val role = enumeration<Role>("role")
    val username = text("username")
}