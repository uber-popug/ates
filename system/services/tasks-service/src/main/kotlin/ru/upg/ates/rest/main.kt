package ru.upg.ates.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import ru.upg.ates.model.DomainConfig
import ru.upg.ates.rest.handler.CreateTaskHandler
import ru.upg.ates.rest.handler.ListTasksHandler
import ru.upg.ates.rest.handler.ReassignAllTasksHandler
import ru.upg.ates.tasks.TasksContext


val tasksServiceApp = { domain: TasksContext ->
    val mapper = jacksonObjectMapper()

    // setting Tasks service REST API
    //  1) list tasks           GET  /tasks?showFinished&search&user&page&sort
    //  2) creating task        POST /tasks
    //  3) reassign all tasks   POST /tasks/reassign
    //  4) finish task          POST /tasks/{id}/finish
    routes(
        "/ping" bind Method.GET to { Response(Status.OK).body("pong") },
        "/tasks" bind routes(
            Method.GET bind ListTasksHandler(mapper, domain),
            Method.POST bind CreateTaskHandler(mapper, domain),
            "/reassign" bind Method.POST to ReassignAllTasksHandler(mapper, domain),
//                "/{id}/finish" bind handlers.finishTask,
        )
    )
}

fun main() {
    val domain = buildDomain()

    tasksServiceApp(domain)
        .asServer(Undertow(8801))
        .start()
}

private fun buildDomain(): TasksContext {
    return TasksContext(
        tables, DomainConfig(
            kafkaUrl = "http://localhost:9994",
            db = DomainConfig.Db(
                url = "jdbc:postgresql://localhost:5432/ates_tasks",
                username = "postgres",
                password = "postgres"
            )
        )
    )
}

