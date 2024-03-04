package ru.upg.ates.tasks.command

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.upg.ates.event.TaskCUD
import ru.upg.ates.event.TaskChange
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.model.Task
import ru.upg.ates.tasks.query.GetTask
import ru.upg.ates.common.cqrs.Command
import ru.upg.ates.common.cqrs.IAggregate
import ru.upg.ates.common.ddd.execute
import ru.upg.ates.common.ddd.fetch
import ru.upg.ates.common.events.Event
import java.util.*

class UpdateTaskCommand(
    override val aggregate: Aggregate
) : Command<TasksDomain, UpdateTaskCommand.Aggregate, Task>() {

    data class Aggregate(
        val id: Long,
        val userPid: UUID?,
        val price: Int?,
        val name: String?,
    ) : IAggregate

    override fun execute(domain: TasksDomain): Pair<Task, List<Event>> {
        val actualTask = domain.fetch(GetTask(aggregate.id))

        val change = TaskChange(
            pid = actualTask.pid,
            userPid = aggregate.userPid ?: actualTask.userPid,
            name = aggregate.name ?: actualTask.name,
            price = aggregate.price ?: actualTask.price,
            finished = false
        )

        transaction {
            domain.tables.tasks.let { table ->
                table.update({ table.id eq aggregate.id }) { update ->
                    update[userPid] = change.userPid
                    update[price] = change.price
                    update[name] = change.name
                }
            }
        }

        return Task(aggregate.id, change) to listOf(TaskCUD.Updated(change))
    }
}