package ru.upg.ates.events

import ru.upg.ates.BusinessEvent
import ru.upg.ates.CUDEvent
import java.util.*

data class TaskChange(
    val pid: UUID,
    val userPid: UUID,
    val name: String,
    val price: Int,
    val finished: Boolean
)

sealed interface TaskCUD : CUDEvent {
    data class Created(val task: TaskChange) : TaskCUD
    data class Updated(val task: TaskChange) : TaskCUD
}

interface TaskBE : BusinessEvent {
    data class Assigned(val taskPid: UUID, val userPid: UUID) : TaskBE
    data class Finished(val taskPid: UUID) : TaskBE
}
