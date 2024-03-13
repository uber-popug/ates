package ru.upg.ates.model

data class PageableList<T>(
    val page: Long,
    val pageSize: Int,
    val total: Long,
    val entities: List<T>,
)
