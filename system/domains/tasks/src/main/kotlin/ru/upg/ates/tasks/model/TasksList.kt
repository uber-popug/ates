package ru.upg.ates.tasks.model

class TasksList(
    val tasks: List<Task>,
    val page: Long,
    val pageSize: Int,
    val total: Long,
)
