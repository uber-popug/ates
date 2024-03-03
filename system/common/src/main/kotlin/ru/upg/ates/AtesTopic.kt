package ru.upg.ates

import ru.upg.ates.event.TaskBE
import ru.upg.ates.event.TaskCUD
import ru.upg.ates.event.UserCUD
import kotlin.reflect.KClass

/**
 * Naming based on pattern
 * domain.classification.description
 *
 * Where:
 * - possible domains [auth, tasks]
 * - classification: [cdc - Change Data Capture, res - result of command execution]
 */
sealed class AtesTopic<T : Any>(val value: String, val event: KClass<T>) {
    // topics with CUD events
    data object Users : AtesTopic<UserCUD>("auth.cdc.user", UserCUD::class)
    data object Tasks : AtesTopic<TaskCUD>("tasks.cdc.task", TaskCUD::class)

    // topics with BE events
    data object  TaskAssigned: AtesTopic<TaskBE.Assigned>("tasks.res.assigned", TaskBE.Assigned::class)
    data object  TaskFinished: AtesTopic<TaskBE.Finished>("tasks.res.finished", TaskBE.Finished::class)
}
