package ru.upg.ates.billing.table

import ru.upg.ates.table.AtesTable

object TaskTable : AtesTable("task") {
    val assignedToId = long("assigned_to_id").references(UserTable.id)
    val title = text("title")
    val assignPrice = long("assign_price")
    val finishPrice = long("finish_price")
    val finished = bool("finished").default(false)
}
