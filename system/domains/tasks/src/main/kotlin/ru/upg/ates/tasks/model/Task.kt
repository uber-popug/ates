package ru.upg.ates.tasks.model

import org.jetbrains.exposed.sql.ResultRow
import ru.upg.ates.event.TaskChange
import ru.upg.ates.tasks.table.TaskTable
import ru.upg.common.cqrs.ReadModel
import java.util.*

data class Task(
    val id: Long,
    val pid: UUID,
    val name: String,
    val price: Int,
    val finished: Boolean,
) : ReadModel {

    constructor(table: TaskTable, row: ResultRow) : this(
        id = row[table.id].value,
        pid = row[table.pid],
        name = row[table.name],
        price = row[table.price],
        finished = row[table.finished]
    )

    constructor(id: Long, change: TaskChange) : this(
        id = id,
        pid = change.pid,
        name = change.name,
        price = change.price,
        finished = change.finished
    )
}