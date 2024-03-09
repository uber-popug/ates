package ru.upg.ates.tasks.command

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.Topic
import ru.upg.ates.events.TaskAssigned
import ru.upg.ates.execute
import ru.upg.ates.tasks.TasksContext
import ru.upg.ates.tasks.model.Task
import ru.upg.ates.tasks.query.GetRandomWorkers
import ru.upg.ates.tasks.table.TaskTable
import ru.upg.ates.tasks.table.UserTable
import java.time.Instant

object ReassignAllTasks : Command.Silent<TasksContext> {
    override fun execute(context: TasksContext) = with(context) {
        val (tasks, users) = TaskTable to UserTable
        transaction {
            val notFinishedTasks =
                tasks.leftJoin(users).selectAll()
                    .andWhere { tasks.finished eq false }
                    .map { Task(tasks, users, it) }

            val workerIds = execute(GetRandomWorkers(notFinishedTasks.size))
            val changes = workerIds.mapIndexed { index, workerId ->
                workerId to notFinishedTasks[index]
            }

            val now = Instant.now()
            val exclude = TaskTable.columns - TaskTable.assignedTo
            tasks.batchUpsert(changes, onUpdateExclude = exclude) { (workerId, task) ->
                this[tasks.id] = task.id
                this[tasks.pid] = task.pid
                this[tasks.assignedTo] = workerId.id
                this[tasks.title] = task.name
                this[tasks.createdAt] = now
                this[tasks.updatedAt] = now
                this[tasks.finished] = false
            }

            changes.forEach { (workerId, task) ->
                publish(Topic.TASK_ASSIGNED, TaskAssigned(task.pid, workerId.pid))
            }
        }
    }
}
