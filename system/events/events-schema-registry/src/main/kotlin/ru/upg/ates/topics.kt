package ru.upg.ates

interface Topic {
    val value: String
}

enum class AtesTopic(override val value: String) : Topic {
    NOT_FOUND("not-found"),

    // topics with CUD events
    USERS("users"),
    TASKS("tasks"),
    BALANCE_CHANGES("balance-changes"),
    PAYMENTS("payments"),
    EMAILS("emails"),

    // topics with BE events
    TASK_ASSIGNED("tasks.assigned"),
    TASK_FINISHED("tasks.finished"),
    BALANCE_CHANGED("balance.changed"),
    PAYMENT_CREATED("payment.created"),
}
