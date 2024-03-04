package ru.upg.ates.tasks.command

import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.Event
import ru.upg.ates.events.TaskBE
import ru.upg.ates.events.TaskCUD
import ru.upg.ates.events.TaskChange
import ru.upg.ates.fetch
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.model.Task
import ru.upg.ates.tasks.query.GetRandomWorkersQuery
import ru.upg.ates.tasks.query.GetTaskQuery
import ru.upg.ates.tasks.table.TaskTable
import java.util.*

class CreateTaskCommand(
    private val name: String
) : Command<TasksDomain, Task> {

    override fun execute(domain: TasksDomain): Pair<Task, List<Event>> {
        val query = GetRandomWorkersQuery(1)

        val workerId = domain.fetch(query).firstOrNull()
            ?: throw IllegalStateException("No one worker was found")

        val change = TaskChange(
            pid = UUID.randomUUID(),
            userPid = workerId.pid,
            name = this.name,
            price = randomPrice(),
            finished = false
        )

        val task = transaction {
            val createdId = TaskTable.insertAndGetId {
                it[pid] = change.pid
                it[userId] = workerId.id
                it[name] = change.name
                it[price] = change.price
                it[finished] = change.finished
            }

            domain.fetch(GetTaskQuery(createdId.value))
        }

        return task to listOf(
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