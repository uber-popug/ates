package ru.upg.ates.tasks.query

import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.table.getById
import ru.upg.ates.tasks.TasksContext
import ru.upg.ates.tasks.model.User
import ru.upg.ates.tasks.table.UserTable

class GetUser(private val id: Long) : Query<TasksContext, User> {
    override fun execute(context: TasksContext): User {
        return transaction {
            UserTable.getById(this@GetUser.id, ::User)
                ?: throw IllegalArgumentException("Not found user by id $id")
        }
    }
}
