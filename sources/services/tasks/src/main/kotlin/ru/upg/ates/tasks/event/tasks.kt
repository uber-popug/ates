package ru.upg.ates.tasks.event

import ru.upg.ates.tasks.model.Task
import java.util.UUID


data class TaskCreated(
    val task: Task
)

data class TaskUpdated(
    val task: Task
)

data class TaskDeleted(
    val taskId: UUID
)
