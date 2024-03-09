package ru.upg.ates.tasks.command

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.upg.ates.Command
import ru.upg.ates.Topic
import ru.upg.ates.events.TaskFinished
import ru.upg.ates.execute
import ru.upg.ates.tasks.TasksContext
import ru.upg.ates.tasks.model.Task
import ru.upg.ates.tasks.query.GetTask
import ru.upg.ates.tasks.table.TaskTable
import java.time.Instant

class FinishTask(private val taskId: Long) : Command<TasksContext, Task> {
    override fun execute(context: TasksContext): Task {
        return transaction {
            val now = Instant.now()
            
            TaskTable.update({ TaskTable.id eq taskId }) {
                it[finished] = true
                it[updatedAt] = now
                it[finishedAt] = now
            }
            
            val task = context.execute(GetTask(taskId))
            val event = TaskFinished(
                taskPid = task.pid,
                finishedBy = task.assignedTo.pid
            )
            
            context.publish(Topic.TASK_FINISHED, event)
            task
        }
    }
}
