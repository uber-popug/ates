package ru.upg.ates.billing.query

import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.model.User
import ru.upg.ates.billing.table.UserTable
import ru.upg.ates.table.getByPid
import java.util.*

class GetUser(private val userPid: UUID) : Query<BillingContext, User> {
    override fun execute(context: BillingContext): User {
        return transaction {
            UserTable.getByPid(userPid, ::User)
                ?: throw IllegalArgumentException("Not found user by id $id")
        }
    }
}
