package ru.upg.ates.events

import java.time.LocalDateTime
import java.util.*

data class TaskChange(
    val pid: UUID,
    val userPid: UUID,
    val name: String,
    val price: Int,
    val finished: Boolean
)

sealed class TaskCUD(
    override val id: UUID = UUID.randomUUID(),
    override val timestamp: LocalDateTime = LocalDateTime.now(),
) : CUDEvent<TaskChange> {

    data class Created(override val payload: TaskChange) : TaskCUD() {
        override val jsonSchemaId = "task-created-1"
        override val name = "TaskCreated"
        override val version = 1
        override val producer = "tasks"
    }

    data class Updated(override val payload: TaskChange) : TaskCUD() {
        override val jsonSchemaId = "task-updated-1"
        override val name = "TaskUpdated"
        override val version = 1
        override val producer = "tasks"
    }
}

abstract class TaskBE<T>(
    override val id: UUID = UUID.randomUUID(),
    override val timestamp: LocalDateTime = LocalDateTime.now(),
) : BusinessEvent<T> {

    data class Assigned(override val payload: Payload) : TaskBE<Assigned.Payload>() {
        data class Payload(val taskPid: UUID, val userPid: UUID)

        override val jsonSchemaId = "task-assigned-1"
        override val name = "TaskAssigned"
        override val version = 1
        override val producer = "tasks"
    }

    data class Finished(override val payload: Payload) : TaskBE<Finished.Payload>() {
        data class Payload(val taskPid: UUID)

        override val jsonSchemaId = "task-finished-1"
        override val name = "TaskFinished"
        override val version = 1
        override val producer = "tasks"
    }
}
