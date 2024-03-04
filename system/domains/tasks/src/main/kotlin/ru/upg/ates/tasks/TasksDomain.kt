package ru.upg.ates.tasks

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.AtesTopic
import ru.upg.ates.Domain
import ru.upg.ates.KafkaEventsBroker
import ru.upg.ates.events.TaskBE
import ru.upg.ates.events.TaskCUD
import ru.upg.ates.events.UserCUD
import ru.upg.ates.handler
import ru.upg.ates.model.DomainConfig
import ru.upg.ates.tasks.command.SaveUserCommand
import ru.upg.ates.tasks.table.TaskTable
import ru.upg.ates.tasks.table.UserTable

class TasksDomain(
    val tables: Tables,
    val config: DomainConfig,
) : Domain {

    // event broker configuration

    override val broker = KafkaEventsBroker(
        url = config.kafkaUrl,
        notFoundTopic = AtesTopic.NOT_FOUND,
        card = mapOf(
            TaskCUD.Created::class to AtesTopic.TASKS,
            TaskCUD.Updated::class to AtesTopic.TASKS,
            TaskBE.Assigned::class to AtesTopic.TASK_ASSIGNED,
            TaskBE.Finished::class to AtesTopic.TASK_FINISHED
        )
    )

    private val listener = broker.listener("ates-tasks")
        .register(AtesTopic.USERS, UserCUD::class, handler(::SaveUserCommand))
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
