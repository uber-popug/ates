package ru.upg.ates.tasks.query

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.events.Role
import ru.upg.ates.model.EntityId
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.table.UserTable

class GetRandomWorkersQuery(
    private val amount: Int
) : Query<TasksDomain, List<EntityId>> {

    override fun execute(domain: TasksDomain): List<EntityId> {
        val allWorkers = transaction {
            domain.tables.users.let { users ->
                users.select(users.id, users.pid)
                .andWhere { UserTable.role eq Role.WORKER }
                .map { EntityId(users, it) }
            }
        }

        return mutableListOf<EntityId>().also { users ->
            repeat(amount) {
                val randomWorker = allWorkers.indices.random()
                users.add(allWorkers[randomWorker])
            }
        }
    }
}