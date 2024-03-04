package ru.upg.ates.auth.operation

import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.auth.AuthDomain
import ru.upg.ates.auth.model.User
import ru.upg.ates.events.Role
import ru.upg.ates.events.UserCUD
import ru.upg.ates.events.UserChange
import ru.upg.ates.Command
import ru.upg.ates.cqrs.IAggregate
import ru.upg.ates.Event
import java.util.*

class RegisterUser(
    override val aggregate: Aggregate
): Command<AuthDomain, RegisterUser.Aggregate, User>() {

    data class Aggregate(
        val role: Role,
        val username: String,
        val password: String
    ): IAggregate

    override fun execute(domain: AuthDomain): Pair<User, List<Event>> {
        val change = UserChange(
            pid = UUID.randomUUID(),
            role = aggregate.role,
            username = aggregate.username
        )

        val id = transaction {
            domain.tables.users.let { table ->
                table.insertAndGetId {
                    it[pid] = change.pid
                    it[role] = aggregate.role
                    it[username] = aggregate.username
                    it[password] = aggregate.password
                }
            }
        }

        return User(id.value, change) to listOf(UserCUD.Created(change))
    }
}