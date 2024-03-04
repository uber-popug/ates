package ru.upg.ates.tasks.model

import ru.upg.common.cqrs.ReadModel

class TasksList(
    val tasks: List<Task>,
    val page: Long,
    val pageSize: Int,
    val total: Long,
) : ReadModel
