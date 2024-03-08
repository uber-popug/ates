package ru.upg.ates.tasks.command

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.upg.ates.Command
import ru.upg.ates.events.Event
import ru.upg.ates.events.TaskBE
import ru.upg.ates.events.TaskChanged
import ru.upg.ates.events.Change
import ru.upg.ates.fetch
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.model.Task
import ru.upg.ates.tasks.query.GetTaskQuery
import ru.upg.ates.tasks.query.GetUserQuery

class UpdateTaskCommand(
    private val id: Long,
    private val userId: Long?,
    private val price: Int?,
    private val name: String?,
) : Command<TasksDomain, Task> {

    override fun execute(context: TasksDomain): Pair<Task, List<Event>> {
        val actualTask = context.fetch(GetTaskQuery(id))
        val targetUser = userId?.let { context.fetch(GetUserQuery(it)) }

        val change = Change(
            pid = actualTask.pid,
            userPid = targetUser?.pid ?: actualTask.assignedTo.pid,
            name = name ?: actualTask.name,
            price = price ?: actualTask.price,
            finished = false
        )

        val updatedTask = transaction {
            context.tables.tasks.let { tasks ->
                tasks.update({ tasks.id eq this@UpdateTaskCommand.id }) { update ->
                    update[assignedTo] = targetUser?.id ?: actualTask.assignedTo.id
                    update[price] = change.price
                    update[name] = change.name
                }
            }

            context.fetch(GetTaskQuery(this@UpdateTaskCommand.id))
        }

        val events = mutableListOf<Event>(TaskChanged.Updated(change))
        if (actualTask.assignedTo.id != updatedTask.assignedTo.id) {
            events.add(TaskBE.TaskAssigned(updatedTask.pid, updatedTask.assignedTo.pid))
        }

        return updatedTask to events
    }
}
