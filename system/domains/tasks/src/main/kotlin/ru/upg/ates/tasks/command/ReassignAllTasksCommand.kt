package ru.upg.ates.tasks.command

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.events.Event
import ru.upg.ates.events.TaskBE
import ru.upg.ates.fetch
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.model.Task
import ru.upg.ates.tasks.query.GetRandomWorkersQuery

class ReassignAllTasksCommand : Command<TasksDomain, Unit> {
    override fun execute(domain: TasksDomain): Pair<Unit, List<Event>> {
        val (tasks, users) = domain.tables
        return Unit to transaction {
            val notFinishedTasks =
                tasks.leftJoin(users).selectAll()
                    .andWhere { tasks.finished eq false }
                    .map { Task(tasks, users, it) }

            val workerIds = domain.fetch(GetRandomWorkersQuery(notFinishedTasks.size))
            val changes = workerIds.mapIndexed { index, workerId ->
                workerId to notFinishedTasks[index]
            }

            val exclude = listOf(tasks.pid, tasks.price, tasks.name, tasks.finished)
            tasks.batchUpsert(changes, onUpdateExclude = exclude) { (workerId, task) ->
                this[tasks.id] = task.id
                this[tasks.pid] = task.pid
                this[tasks.userId] = workerId.id
                this[tasks.name] = task.name
                this[tasks.price] = task.price
                this[tasks.finished] = false
            }

            changes.map { (workerId, task) ->
                TaskBE.Assigned(task.pid, workerId.pid)
            }
        }
    }
}
