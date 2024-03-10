package ru.upg.ates.analytic.table

import ru.upg.ates.table.AtesTable

object TaskTable : AtesTable("task") {
    val userId = long("user_id").references(UserTable.id)
    val title = text("title")
    val finished = bool("finished").default(false)
}
