package ru.upg.ates.events

import ru.upg.ates.Event
import java.time.Instant
import java.util.*

data class TaskCreated(
    val pid: UUID,
    val userPid: UUID,
    val title: String,
    val finished: Boolean,
    override val id: UUID = UUID.randomUUID(),
    override val timestamp: Instant = Instant.now()
) : Event {
    override val jsonSchemaId = "#/tasks/created/1"
    override val name = "TaskCreated"
    override val version = 1
    override val producer = "tasks"
}

data class TaskAssigned(
    val taskPid: UUID,
    val assignedTo: UUID,
    override val id: UUID = UUID.randomUUID(),
    override val timestamp: Instant = Instant.now()
) : Event {
    override val jsonSchemaId = "#/tasks/assigned/1"
    override val name = "TaskAssigned"
    override val version = 1
    override val producer = "tasks"
}

data class TaskFinished(
    val taskPid: UUID,
    val finishedBy: UUID,
    override val id: UUID = UUID.randomUUID(),
    override val timestamp: Instant = Instant.now()
) : Event {
    override val jsonSchemaId = "#/tasks/finished/1"
    override val name = "TaskFinished"
    override val version = 1
    override val producer = "tasks"
}
