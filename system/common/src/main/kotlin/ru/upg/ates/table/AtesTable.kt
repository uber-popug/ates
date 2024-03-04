package ru.upg.ates.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll

open class AtesTable(name: String) : LongIdTable(name) {
    val pid = uuid("pid").uniqueIndex()
}

fun <T : AtesTable, E> T.getById(id: Long, cnstr: (T, ResultRow) -> E): E? {
    return this.selectAll()
        .andWhere { this@getById.id eq id }
        .firstOrNull()
        ?.let { cnstr(this@getById, it) }
}