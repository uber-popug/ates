package ru.upg.ates.auth.operation

import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.auth.AuthContext
import ru.upg.ates.auth.model.User
import ru.upg.ates.auth.table.UserTable
import ru.upg.ates.events.Role
import ru.upg.ates.events.UserCreated
import java.util.*

class RegisterUser(
    private val role: Role,
    private val username: String,
    private val password: String
) : Command<AuthContext, User> {

    override fun execute(context: AuthContext): User {
        return transaction {
            val userPid = UUID.randomUUID()
            
            val userId = UserTable.insertAndGetId {
                it[pid] = userPid
                it[role] = this@RegisterUser.role
                it[username] = this@RegisterUser.username
                it[password] = this@RegisterUser.password
            }

            UserCreated(userPid, role, username)
                .also(context::publish)
                .let { User(userId.value, it) }
        }
    }
}
