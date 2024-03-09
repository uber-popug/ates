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

fun main() {
    val infra = AtesInfra(InfraConfig.local)
    
    val config = infra.config.services.tasks
    infra.config.databases.tasks.run {
        Database.connect(url, username, password)
    }
    
    val domain = TasksContext(config.name, infra.kafka)

    val app = infra.taskService(
        AtesInfra.TasksHandlers(
            listTasks = ListTasksHandler(infra.httpMapper, domain),
            createTask = CreateTaskHandler(infra.httpMapper, domain),
            reassignTasks = ReassignAllTasksHandler(infra.httpMapper, domain),
            finishTask = FinishTaskHandler(infra.httpMapper, domain)
        )
    )
    
    

    app.asServer(Undertow(config.port)).start()
}
