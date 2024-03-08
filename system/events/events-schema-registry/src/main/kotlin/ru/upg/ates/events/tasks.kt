package ru.upg.ates.events

import java.util.*

sealed class TaskChanged : Event<TaskChanged.Payload>() {

    data class Payload(
        val pid: UUID,
        val userPid: UUID,
        val name: String,
        val finished: Boolean
    )

    data class Created(override val payload: Payload) : TaskChanged() {
        override val jsonSchemaId = "#/tasks/created/1"
        override val name = "TaskCreated"
        override val version = 1
        override val producer = "tasks"
    }

    data class Updated(override val payload: Payload) : TaskChanged() {
        override val jsonSchemaId = "#/tasks/updated/1"
        override val name = "TaskUpdated"
        override val version = 1
        override val producer = "tasks"
    }
}

data class TaskAssigned(override val payload: Payload) : Event<TaskAssigned.Payload>() {
    data class Payload(val taskPid: UUID, val assignedTo: UUID)

    override val jsonSchemaId = "#/tasks/assigned/1"
    override val name = "TaskAssigned"
    override val version = 1
    override val producer = "tasks"
}

data class TaskFinished(override val payload: Payload) : Event<TaskFinished.Payload>() {
    data class Payload(val taskPid: UUID, val finishedBy: UUID)

    override val jsonSchemaId = "#/tasks/finished/1"
    override val name = "TaskFinished"
    override val version = 1
    override val producer = "tasks"
}
