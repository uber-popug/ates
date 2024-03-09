package ru.upg.ates.tasks.table

import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.javatime.timestampParam
import ru.upg.ates.table.AtesTable

object TaskTable : AtesTable("task") {
    val assignedTo = long("assigned_to_id").references(UserTable.id)
    val title = text("title")
    val finished = bool("finished").default(false)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    val finishedAt = timestamp("finished_at").nullable()
}
