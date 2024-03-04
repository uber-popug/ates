package ru.upg.ates.tasks.table

import org.jetbrains.exposed.dao.id.LongIdTable
import ru.upg.ates.table.AtesTable

object TaskTable : AtesTable("task") {
    val userId = long("user_id").references(UserTable.id)
    val name = text("name")
    val price = integer("price")
    val finished = bool("finished")
}