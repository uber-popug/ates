package ru.upg.ates.auth.operation

import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.events.Event
import ru.upg.ates.auth.AuthDomain
import ru.upg.ates.auth.model.User
import ru.upg.ates.events.Role
import ru.upg.ates.events.UserChanged
import ru.upg.ates.events.UserChange
import java.util.*

class RegisterUser(
    private val role: Role,
    private val username: String,
    private val password: String
): Command<AuthDomain, User> {

    override fun execute(context: AuthDomain): Pair<User, List<Event<*>>> {
        val change = UserChange(
            pid = UUID.randomUUID(),
            role = role,
            username = username
        )

        val id = transaction {
            context.tables.users.let { table ->
                table.insertAndGetId {
                    it[pid] = change.pid
                    it[role] = this@RegisterUser.role
                    it[username] = this@RegisterUser.username
                    it[password] = this@RegisterUser.password
                }
            }
        }

        return User(id.value, change) to listOf(UserChanged.Created(change))
    }
}
