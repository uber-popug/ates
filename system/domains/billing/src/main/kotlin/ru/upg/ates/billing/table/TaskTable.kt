package ru.upg.ates.billing.table

import ru.upg.ates.table.AtesTable

object TaskTable : AtesTable("task") {
    val userId = long("user_id").references(UserTable.id)
    val name = text("name")
    val assignPrice = long("assign_price")
    val finishPrice = long("finish_price")
    val finished = bool("finished").default(false)
}
