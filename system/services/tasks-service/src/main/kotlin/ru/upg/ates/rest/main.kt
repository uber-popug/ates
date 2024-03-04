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

fun main() {
    // todo move to config
    // setting listener to track user changes
    val kafkaServer = "http://localhost:9094"
    /*ListenEvents(kafkaServer, consumerGroup)
        .listen(AtesTopic.Users, SaveUserChange(UsersDao())::save)*/

    val domain = buildDomain(kafkaServer)
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
    ).asServer(Undertow(8801)).start()
}

private fun buildDomain(kafkaUrl: String): TasksDomain {
    val tables = TasksDomain.Tables(
        TaskTable,
        UserTable
    )

    return TasksDomain(tables, kafkaUrl)
}

