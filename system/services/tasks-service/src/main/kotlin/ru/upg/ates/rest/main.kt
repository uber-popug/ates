package ru.upg.ates.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.http4k.core.Method
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import ru.upg.ates.rest.handler.CreateTaskHandler
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.table.TaskTable
import ru.upg.ates.tasks.table.UserTable


val tasksServiceApp = { domain: TasksDomain ->
    val mapper = jacksonObjectMapper()

    // setting Tasks service REST API
    //  1) list tasks           GET  /tasks?showFinished&search&user&page&sort
    //  2) creating task        POST /tasks
    //  3) update task          PUT  /tasks
    //  4) reassign all tasks   POST /tasks/reassign
    //  5) finish task          POST /tasks/{id}/finish
    routes(
        "/tasks" bind routes(
//                handlers.listTasks,
            Method.POST bind CreateTaskHandler(mapper, domain),
//                handlers.updateTask,
//                "/reassign" bind handlers.reassignTasks,
//                "/{id}" bind routes(
//                    "/finish" bind handlers.finishTask,
//                )
        )
    )
}

fun main() {
    val domain = buildDomain()

    tasksServiceApp(domain)
        .asServer(Undertow(8801))
        .start()
}

private fun buildDomain(): TasksDomain {
    val tables = TasksDomain.Tables(
        TaskTable,
        UserTable
    )

    return TasksDomain(tables, TasksDomain.Config(
        kafkaUrl = "http://localhost:9994",
        db = TasksDomain.Config.Db(
            url = "jdbc:postgresql://localhost:5432/ates_tasks",
            username = "postgres",
            password = "postgres"
        )
    ))
}

