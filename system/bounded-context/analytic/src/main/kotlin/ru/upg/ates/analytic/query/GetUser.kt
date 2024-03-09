package ru.upg.ates.analytic.query

import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.analytic.AnalyticContext
import ru.upg.ates.analytic.model.User
import ru.upg.ates.analytic.table.UserTable
import ru.upg.ates.table.getByPid
import java.util.*

class GetUser(private val userPid: UUID) : Query<AnalyticContext, User> {
    override fun execute(context: AnalyticContext): User {
        return transaction {
            UserTable.getByPid(userPid, ::User)
                ?: throw IllegalArgumentException("Not found user by id $id")
        }
    }
}
