package ru.upg.ates.tasks.table

import org.jetbrains.exposed.dao.id.LongIdTable

object TaskTable : LongIdTable("task") {
    val pid = uuid("pid")
    val name = text("name")
    val price = integer("price")
    val finished = bool("finished")
}