package ru.upg.ates.tasks

import ru.upg.ates.tasks.table.TaskTable
import ru.upg.ates.tasks.table.UserTable
import ru.upg.cqrs.Domain
import ru.upg.cqrs.Event

class TasksDomain(
    val tables: Tables,
    override val eventsListener: (Event) -> Unit
) : Domain<TasksDomain> {

    class Tables(
        val tasks: TaskTable,
        val users: UserTable
    )
}