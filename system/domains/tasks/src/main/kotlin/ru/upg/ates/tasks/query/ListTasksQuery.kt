package ru.upg.ates.tasks.query

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.model.Task
import ru.upg.ates.tasks.model.TasksList
import ru.upg.common.cqrs.Query
import java.util.UUID

class ListTasksQuery(
    private val showFinished: Boolean = false,
    private val search: String?,
    private val userPid: UUID?,
    private val page: Long,
    private val pageSize: Int,
) : Query<TasksDomain, TasksList>() {

    private val offset = (page - 1) * pageSize

    override fun execute(domain: TasksDomain): TasksList {
        return transaction {
            val tasksTable = domain.tables.tasks

            var query = tasksTable.selectAll()
            if (!showFinished)
                query = query.andWhere { tasksTable.finished eq false }

            if (search != null)
                query = query.andWhere { tasksTable.name.lowerCase() like "%${search.lowercase()}%" }

            if (userPid != null)
                query = query.andWhere { tasksTable.userPid eq userPid }

            val total = query.copy().count()
            val tasks = query.limit(pageSize, offset).map { Task(tasksTable, it) }
            TasksList(tasks, page, pageSize, total)
        }
    }
}