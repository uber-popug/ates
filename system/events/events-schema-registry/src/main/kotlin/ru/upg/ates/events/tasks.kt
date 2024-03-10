package ru.upg.ates.events

import ru.upg.ates.Event
import java.util.*

@Event("#/tasks/created/1.yaml", "TaskCreated", 1)
data class TaskCreatedV1(
    val pid: UUID,
    val assignedToPid: UUID,
    val title: String,
    val finished: Boolean
)

// added field jiraId
@Event("#/tasks/created/2.yaml", "TaskCreated", 2)
data class TaskCreatedV2(
    val pid: UUID,
    val assignedToPid: UUID,
    val title: String,
    val jiraId: String,
    val finished: Boolean,
)

@Event("#/tasks/assigned/1.yaml", "TaskAssigned", 1)
data class TaskAssigned(
    val taskPid: UUID,
    val assignedToPid: UUID
)

@Event("#/tasks/finished/1.yaml", "TaskFinished", 1)
data class TaskFinished(
    val taskPid: UUID
)
