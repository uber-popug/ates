package ru.upg.ates.tasks.command

import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.Topic
import ru.upg.ates.events.TaskAssigned
import ru.upg.ates.events.TaskCreatedV1
import ru.upg.ates.execute
import ru.upg.ates.tasks.TasksContext
import ru.upg.ates.tasks.model.Task
import ru.upg.ates.tasks.query.GetRandomWorkers
import ru.upg.ates.tasks.query.GetTask
import ru.upg.ates.tasks.table.TaskTable
import java.time.Instant
import java.util.*

class CreateTask(private val taskTitle: String) : Command<TasksContext, Task> {

    override fun execute(context: TasksContext): Task = with(context) {
        transaction {
            val workerId =
                execute(GetRandomWorkers(1))
                    .firstOrNull()
                    ?: throw IllegalStateException("No one worker was found")

            val now = Instant.now()
            val taskPid = UUID.randomUUID()
            val createdId = TaskTable.insertAndGetId {
                it[pid] = taskPid
                it[assignedTo] = workerId.id
                it[title] = taskTitle
                it[finished] = false
                it[createdAt] = now
                it[updatedAt] = now
            }

            publish(Topic.TASKS, TaskCreatedV1(taskPid, workerId.pid, taskTitle, false))
            publish(Topic.TASK_ASSIGNED, TaskAssigned(taskPid, workerId.pid))

            execute(GetTask(createdId.value))
        }
    }
}
