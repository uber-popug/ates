package ru.upg.ates.tasks.command

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.upg.ates.Command
import ru.upg.ates.events.Event
import ru.upg.ates.events.TaskBE
import ru.upg.ates.events.TaskCUD
import ru.upg.ates.events.TaskChange
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

    override fun execute(domain: TasksDomain): Pair<Task, List<Event>> {
        val actualTask = domain.fetch(GetTaskQuery(id))
        val targetUser = userId?.let { domain.fetch(GetUserQuery(it)) }

        val change = TaskChange(
            pid = actualTask.pid,
            userPid = targetUser?.pid ?: actualTask.assignedTo.pid,
            name = name ?: actualTask.name,
            price = price ?: actualTask.price,
            finished = false
        )

        val updatedTask = transaction {
            domain.tables.tasks.let { tasks ->
                tasks.update({ tasks.id eq this@UpdateTaskCommand.id }) { update ->
                    update[assignedTo] = targetUser?.id ?: actualTask.assignedTo.id
                    update[price] = change.price
                    update[name] = change.name
                }
            }

            domain.fetch(GetTaskQuery(this@UpdateTaskCommand.id))
        }

        val events = mutableListOf<Event>(TaskCUD.Updated(change))
        if (actualTask.assignedTo.id != updatedTask.assignedTo.id) {
            events.add(TaskBE.Assigned(updatedTask.pid, updatedTask.assignedTo.pid))
        }

        return updatedTask to events
    }
}
