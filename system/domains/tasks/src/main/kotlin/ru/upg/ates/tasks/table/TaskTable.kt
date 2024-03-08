package ru.upg.ates.tasks.table

import ru.upg.ates.table.AtesTable

object TaskTable : AtesTable("task") {
    val assignedTo = long("assigned_to_id").references(UserTable.id)
    val name = text("name")
    val finished = bool("finished").default(false)
}
