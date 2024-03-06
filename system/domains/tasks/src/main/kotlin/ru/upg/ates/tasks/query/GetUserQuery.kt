package ru.upg.ates.tasks.query

import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.table.getById
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.model.User

class GetUserQuery(private val id: Long) : Query<TasksDomain, User> {
    override fun execute(domain: TasksDomain): User {
        return transaction {
            domain.tables.users
                .getById(this@GetUserQuery.id, ::User)
                ?: throw IllegalArgumentException("Not found user by id $id")
        }
    }
}