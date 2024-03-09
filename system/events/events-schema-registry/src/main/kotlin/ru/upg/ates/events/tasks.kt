package ru.upg.ates.events

import ru.upg.ates.Event
import java.time.Instant
import java.util.*

@Event("#/tasks/created/1", "TaskCreated", 1)
data class TaskCreated(
    val pid: UUID,
    val userPid: UUID,
    val title: String,
    val finished: Boolean
)

@Event("#/tasks/assigned/1", "TaskAssigned", 1)
data class TaskAssigned(
    val taskPid: UUID,
    val assignedTo: UUID
)

@Event("#/tasks/finished/1", "TaskFinished", 1)
data class TaskFinished(
    val taskPid: UUID,
    val finishedBy: UUID
)
