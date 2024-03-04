package ru.upg.ates.tasks.command

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.Event
import ru.upg.ates.events.TaskBE
import ru.upg.ates.fetch
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.query.GetRandomWorkersQuery

class ReassignAllTasksCommand : Command<TasksDomain, Unit> {
    override fun execute(domain: TasksDomain): Pair<Unit, List<Event>> {
        return Unit to transaction {
            domain.tables.tasks.let { tasks ->
                val notFinishedTasks =
                    tasks.select(tasks.id, tasks.pid)
                        .andWhere { tasks.finished eq false }
                        .toList()

                val query = GetRandomWorkersQuery(notFinishedTasks.size)
                val workerIds = domain.fetch(query)
                val changes = workerIds.mapIndexed { index, workerId ->
                    workerId.id to TaskBE.Assigned(
                        taskPid = notFinishedTasks[index][tasks.pid],
                        userPid = workerId.pid
                    )
                }

                tasks.batchUpsert(changes) { (workerId, change) ->
                    this[tasks.pid] = change.taskPid
                    this[tasks.userId] = workerId
                }

                changes.map { it.second }
            }
        }
    }
}