package ru.upg.ates.tasks.command

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.event.TaskCUD
import ru.upg.ates.event.TaskChange
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.query.GetRandomWorkers
import ru.upg.ates.tasks.table.TaskTable
import ru.upg.common.cqrs.Command
import ru.upg.common.events.Event
import ru.upg.common.cqrs.IAggregate
import java.util.*

class CreateTaskCommand(
    override val aggregate: Aggregate,
) : Command<TasksDomain, CreateTaskCommand.Aggregate>() {

    data class Aggregate(val name: String) : IAggregate


    override fun execute(domain: TasksDomain): List<Event> {
        val query = GetRandomWorkers(1)

        val workerId = UUID.randomUUID() /*domain.execute(query).ids.firstOrNull()
            ?: throw IllegalStateException("Не найдено ни одного рабочего")*/

        val change = TaskChange(
            pid = UUID.randomUUID(),
            userPid = workerId,
            name = aggregate.name,
            price = randomPrice(),
            finished = false
        )

        transaction {
            TaskTable.insert {
                it[TaskTable.pid] = change.pid
                it[TaskTable.name] = change.name
                it[TaskTable.price] = change.price
                it[TaskTable.finished] = change.finished
            }
        }

        return listOf(TaskCUD.Created(change))
    }


    companion object {
        private fun randomPrice(): Int {
            return (1..20).random()
        }
    }
}