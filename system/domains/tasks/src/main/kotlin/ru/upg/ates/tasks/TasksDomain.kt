package ru.upg.ates.tasks

import ru.upg.ates.tasks.table.TaskTable
import ru.upg.ates.tasks.table.UserTable
import ru.upg.common.ddd.Domain
import ru.upg.common.events.Event

class TasksDomain(
    val tables: Tables,
    override val eventsListener: (Event) -> Unit
) : Domain<TasksDomain> {

    class Tables(
        val tasks: TaskTable,
        val users: UserTable
    )
}