package ru.upg.ates.rest.handler

import com.fasterxml.jackson.databind.ObjectMapper
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import ru.upg.ates.execute
import ru.upg.ates.tasks.TasksContext
import ru.upg.ates.tasks.command.ReassignAllTasks

class ReassignAllTasksHandler(
    private val mapper: ObjectMapper,
    private val domain: TasksContext,
) : HttpHandler {

    override fun invoke(request: Request): Response {
        domain.execute(ReassignAllTasks)
        return Response(Status.OK)
    }
}
