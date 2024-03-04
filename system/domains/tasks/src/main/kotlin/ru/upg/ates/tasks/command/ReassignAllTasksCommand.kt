package ru.upg.ates.tasks.command

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.event.TaskBE
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.query.GetRandomWorkersQuery
import ru.upg.ates.common.cqrs.Command
import ru.upg.ates.common.cqrs.IAggregate
import ru.upg.ates.common.ddd.fetch
import ru.upg.ates.common.events.Event

class ReassignAllTasksCommand : Command<TasksDomain, IAggregate, Unit>() {
    override val aggregate = IAggregate

    override fun execute(domain: TasksDomain): Pair<Unit, List<Event>> {
        return Unit to transaction {
            domain.tables.tasks.let { tasks ->
                val notFinishedTasks =
                    tasks.select(tasks.id)
                        .andWhere { tasks.finished eq false }
                        .toList()

                val query = GetRandomWorkersQuery(notFinishedTasks.size)

                val changes = domain.fetch(query).ids.mapIndexed { index, userPid ->
                    TaskBE.Assigned(
                        taskPid = notFinishedTasks[index][tasks.pid],
                        userPid = userPid
                    )
                }

                tasks.batchUpsert(changes) { change ->
                    this[tasks.pid] = change.taskPid
                    this[tasks.userPid] = change.userPid
                }

                changes
            }
        }
    }
}