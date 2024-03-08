package ru.upg.ates.events

import ru.upg.ates.Event
import java.util.*

data class TaskCreated(
    val pid: UUID,
    val userPid: UUID,
    val title: String,
    val finished: Boolean,
) : Event {
    override val jsonSchemaId = "#/tasks/created/1"
    override val name = "TaskCreated"
    override val version = 1
    override val producer = "tasks"
}

data class TaskAssigned(
    val taskPid: UUID,
    val assignedTo: UUID,
) : Event {
    override val jsonSchemaId = "#/tasks/assigned/1"
    override val name = "TaskAssigned"
    override val version = 1
    override val producer = "tasks"
}

data class TaskFinished(
    val taskPid: UUID,
    val finishedBy: UUID,
) : Event {
    override val jsonSchemaId = "#/tasks/finished/1"
    override val name = "TaskFinished"
    override val version = 1
    override val producer = "tasks"
}
