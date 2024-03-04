package ru.upg.ates

/**
 * Naming based on pattern
 * domain.classification.description
 *
 * Where:
 * - possible domains [auth, tasks]
 * - classification: [cdc - Change Data Capture, res - result of command execution]
 */
enum class AtesTopic(override val value: String) : Topic {
    NOT_FOUND("not-found"),

    // topics with CUD events
    USERS("auth.cdc.user"),
    TASKS("tasks.cdc.task"),

    // topics with BE events
    TASK_ASSIGNED("tasks.res.assigned"),
    TASK_FINISHED("tasks.res.finished")
}
