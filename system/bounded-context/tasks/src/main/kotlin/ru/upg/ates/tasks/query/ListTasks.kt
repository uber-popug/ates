package ru.upg.ates.tasks.query

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.model.PageableList
import ru.upg.ates.tasks.TasksContext
import ru.upg.ates.tasks.model.Task
import ru.upg.ates.tasks.model.TasksList
import ru.upg.ates.tasks.table.TaskTable
import ru.upg.ates.tasks.table.UserTable

class ListTasks(
    private val showFinished: Boolean = false,
    private val search: String?,
    private val userId: Long?,
    private val page: Long,
    private val pageSize: Int
) : Query<TasksContext, PageableList<Task>> {

    private val offset = (page - 1) * pageSize

    override fun execute(context: TasksContext): PageableList<Task> {
        return transaction {
            val (tasks, users) = TaskTable to UserTable

            var query = tasks.leftJoin(users).selectAll()
            if (!showFinished)
                query = query.andWhere { tasks.finished eq false }

            if (search != null)
                query = query.andWhere { tasks.title.lowerCase() like "%${search.lowercase()}%" }

            if (userId != null)
                query = query.andWhere { tasks.assignedTo eq userId }

            val total = query.copy().count()
            val fetchedTasks = query.limit(pageSize, offset).map { Task(tasks, users, it) }
            PageableList(page, pageSize, total, fetchedTasks)
        }
    }
}
