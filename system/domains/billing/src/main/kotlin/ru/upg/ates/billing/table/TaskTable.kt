package ru.upg.ates.billing.table

import ru.upg.ates.table.AtesTable

object TaskTable : AtesTable("task") {
    val userId = long("user_id").references(UserTable.id)
    val name = text("name")
    val assignPrice = integer("assign_price")
    val finishPrice = integer("finish_price")
    val finished = bool("finished").default(false)
}
