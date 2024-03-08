package ru.upg.ates.tasks

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.AtesTopic
import ru.upg.ates.BoundedContext
import ru.upg.ates.events.TaskBE
import ru.upg.ates.events.TaskChanged
import ru.upg.ates.events.UserChanged
import ru.upg.ates.broker.KafkaEventsBroker
import ru.upg.ates.handler
import ru.upg.ates.model.DomainConfig
import ru.upg.ates.tasks.command.SaveUserCommand
import ru.upg.ates.tasks.table.TaskTable
import ru.upg.ates.tasks.table.UserTable

class TasksDomain(
    val tables: Tables,
    val config: DomainConfig,
) : BoundedContext {

    // event broker configuration

    override val broker = KafkaEventsBroker(
        url = config.kafkaUrl,
        notFoundTopic = AtesTopic.NOT_FOUND,
        card = mapOf(
            TaskChanged.Created::class to AtesTopic.TASKS,
            TaskChanged.Updated::class to AtesTopic.TASKS,
            TaskBE.TaskAssigned::class to AtesTopic.TASK_ASSIGNED,
            TaskBE.TaskFinished::class to AtesTopic.TASK_FINISHED
        )
    )

    private val listener = broker.listener("ates-tasks")
        .register(AtesTopic.USERS, UserChanged::class, handler(::SaveUserCommand))
        .listen()


    // persistence configuration

    data class Tables(
        val tasks: TaskTable,
        val users: UserTable
    )

    private val database = config.db.run {
        Database.connect(
            url = url,
            user = username,
            password = password
        )

        // todo: setting migrations
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                tables.users,
                tables.tasks,
            )
        }
    }
}
