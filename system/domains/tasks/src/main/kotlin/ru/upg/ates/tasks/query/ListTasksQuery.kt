package ru.upg.ates.tasks.query

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.model.Task
import ru.upg.ates.tasks.model.TasksList

class ListTasksQuery(
    private val showFinished: Boolean = false,
    private val search: String?,
    private val userId: Long?,
    private val page: Long,
    private val pageSize: Int,
) : Query<TasksDomain, TasksList> {

    private val offset = (page - 1) * pageSize

    override fun execute(context: TasksDomain): TasksList {
        return transaction {
            val (tasks, users) = context.tables

            var query = tasks.leftJoin(users).selectAll()
            if (!showFinished)
                query = query.andWhere { tasks.finished eq false }

            if (search != null)
                query = query.andWhere { tasks.name.lowerCase() like "%${search.lowercase()}%" }

            if (userId != null)
                query = query.andWhere { tasks.assignedTo eq userId }

            val total = query.copy().count()
            val fetchedTasks = query.limit(pageSize, offset).map { Task(tasks, users, it) }
            TasksList(fetchedTasks, page, pageSize, total)
        }
    }
}
