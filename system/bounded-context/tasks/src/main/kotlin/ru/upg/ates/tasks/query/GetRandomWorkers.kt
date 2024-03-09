package ru.upg.ates.tasks.query

import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Query
import ru.upg.ates.events.Role
import ru.upg.ates.model.EntityId
import ru.upg.ates.tasks.TasksContext
import ru.upg.ates.tasks.table.UserTable

class GetRandomWorkers(
    private val amount: Int
) : Query<TasksContext, List<EntityId>> {

    override fun execute(context: TasksContext): List<EntityId> {
        val allWorkers = transaction {
            UserTable.select(UserTable.id, UserTable.pid)
                .andWhere { UserTable.role eq Role.WORKER }
                .map { EntityId(UserTable, it) }
        }

        return mutableListOf<EntityId>().also { users ->
            repeat(amount) {
                val randomWorker = (0..<allWorkers.count()).random()
                users.add(allWorkers[randomWorker])
            }
        }
    }
}
