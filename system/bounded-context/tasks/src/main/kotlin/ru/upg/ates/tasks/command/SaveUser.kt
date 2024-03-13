package ru.upg.ates.tasks.command

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import ru.upg.ates.Command
import ru.upg.ates.events.UserCreated
import ru.upg.ates.tasks.TasksContext
import ru.upg.ates.tasks.table.UserTable

class SaveUser(private val event: UserCreated) : Command.Silent<TasksContext> {
    override fun execute(context: TasksContext) {
        transaction {
            UserTable.upsert(keys = arrayOf(UserTable.pid)) {
                it[pid] = event.pid
                it[username] = event.username
                it[role] = event.role
            }
        }
    }
}
