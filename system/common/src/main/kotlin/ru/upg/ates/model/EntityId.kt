package ru.upg.ates.model

import org.jetbrains.exposed.sql.ResultRow
import ru.upg.ates.table.AtesTable
import java.util.UUID

data class EntityId(
    val id: Long,
    val pid: UUID,
) {
    constructor(table: AtesTable, row: ResultRow) : this(
        id = row[table.id].value,
        pid = row[table.pid]
    )
}
