package ru.upg.ates.tasks.queries

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.event.Role
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.table.UserTable
import ru.upg.cqrs.Query
import ru.upg.cqrs.ReadModel
import java.util.UUID

class GetRandomWorkers(
    private val amount: Int
) : Query<TasksDomain, GetRandomWorkers.WorkerIds>() {

    @JvmInline
    value class WorkerIds(val ids: List<UUID>) : ReadModel

    override fun execute(domain: TasksDomain): WorkerIds {
        val workers = transaction {
            domain.tables.users
                .selectAll()
                .andWhere { UserTable.role eq Role.WORKER }
                .map { it[UserTable.id].value }
        }

        val ids = mutableListOf<UUID>().also { users ->
            repeat(amount) {
                val randomWorker = (0..workers.size).random()
                users.add(workers[randomWorker])
            }
        }

        return WorkerIds(ids)
    }
}