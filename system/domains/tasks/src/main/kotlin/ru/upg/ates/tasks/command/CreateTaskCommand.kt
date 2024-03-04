package ru.upg.ates.tasks.command

import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.event.TaskBE
import ru.upg.ates.event.TaskCUD
import ru.upg.ates.event.TaskChange
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.model.Task
import ru.upg.ates.tasks.query.GetRandomWorkersQuery
import ru.upg.ates.tasks.table.TaskTable
import ru.upg.ates.common.cqrs.Command
import ru.upg.ates.common.cqrs.IAggregate
import ru.upg.ates.common.ddd.fetch
import ru.upg.ates.common.events.Event
import java.util.*

class CreateTaskCommand(
    override val aggregate: Aggregate,
) : Command<TasksDomain, CreateTaskCommand.Aggregate, Task>() {

    data class Aggregate(val name: String) : IAggregate


    override fun execute(domain: TasksDomain): Pair<Task, List<Event>> {
        val query = GetRandomWorkersQuery(1)

        val workerId = domain.fetch(query).ids.firstOrNull()
            ?: throw IllegalStateException("Не найдено ни одного рабочего")

        val change = TaskChange(
            pid = UUID.randomUUID(),
            userPid = workerId,
            name = aggregate.name,
            price = randomPrice(),
            finished = false
        )

        val id = transaction {
            TaskTable.insertAndGetId {
                it[pid] = change.pid
                it[userPid] = change.userPid
                it[name] = change.name
                it[price] = change.price
                it[finished] = change.finished
            }
        }

        return Task(id.value, change) to listOf(
            TaskCUD.Created(change),
            TaskBE.Assigned(change.pid, change.userPid)
        )
    }


    companion object {
        private fun randomPrice(): Int {
            return (1..20).random()
        }
    }
}