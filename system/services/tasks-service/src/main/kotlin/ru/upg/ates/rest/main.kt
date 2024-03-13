package ru.upg.ates.rest

import org.http4k.server.Undertow
import org.http4k.server.asServer
import ru.upg.ates.AtesInfra
import ru.upg.ates.InfraConfig
import ru.upg.ates.rest.handler.CreateTaskHandler
import ru.upg.ates.rest.handler.FinishTaskHandler
import ru.upg.ates.rest.handler.ListTasksHandler
import ru.upg.ates.rest.handler.ReassignAllTasksHandler
import ru.upg.ates.tasks.TasksContext
import ru.upg.ates.tasks.table.TaskTable
import ru.upg.ates.tasks.table.UserTable

fun main() {
    val config = InfraConfig.local
    val dbConfig = config.databases.tasks
    val serviceConfig = config.services.tasks
    
    val infra = AtesInfra(config).apply {
        initDatabase(dbConfig, UserTable, TaskTable)
    }
    
    val context = TasksContext(serviceConfig.name, infra.kafka)

    val app = infra.taskService(
        AtesInfra.TasksHandlers(
            listTasks = ListTasksHandler(infra.httpMapper, context),
            createTask = CreateTaskHandler(infra.httpMapper, context),
            reassignTasks = ReassignAllTasksHandler(infra.httpMapper, context),
            finishTask = FinishTaskHandler(infra.httpMapper, context)
        )
    )
    
    app.asServer(Undertow(serviceConfig.port)).start()
}
