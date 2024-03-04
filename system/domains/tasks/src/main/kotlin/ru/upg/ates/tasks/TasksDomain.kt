package ru.upg.ates.tasks

import ru.upg.ates.AtesTopic
import ru.upg.ates.event.TaskBE
import ru.upg.ates.event.TaskCUD
import ru.upg.ates.event.UserCUD
import ru.upg.ates.tasks.command.SaveUser
import ru.upg.ates.tasks.table.TaskTable
import ru.upg.ates.tasks.table.UserTable
import ru.upg.common.ddd.Domain
import ru.upg.common.ddd.handler
import ru.upg.common.events.KafkaEventsBroker

class TasksDomain(
    val tables: Tables,
    kafkaUrl: String,
) : Domain<TasksDomain> {

    class Tables(
        val tasks: TaskTable,
        val users: UserTable
    )

    override val broker = KafkaEventsBroker(
        url = kafkaUrl,
        notFoundTopic = AtesTopic.NOT_FOUND,
        card = mapOf(
            TaskCUD::class to AtesTopic.TASKS,
            TaskBE.Assigned::class to AtesTopic.TASK_ASSIGNED,
            TaskBE.Finished::class to AtesTopic.TASK_FINISHED
        )
    )

    private val listener = broker.listen("ates-tasks").apply {
        register(AtesTopic.USERS, UserCUD::class, handler(::SaveUser))
        start()
    }
}
