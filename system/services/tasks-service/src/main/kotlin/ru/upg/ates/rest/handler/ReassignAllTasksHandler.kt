package ru.upg.ates.rest.handler

import com.fasterxml.jackson.databind.ObjectMapper
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.command.ReassignAllTasksCommand
import ru.upg.ates.common.ddd.execute

class ReassignAllTasksHandler(
    private val mapper: ObjectMapper,
    private val domain: TasksDomain,
) : HttpHandler {

    override fun invoke(request: Request): Response {
        domain.execute(ReassignAllTasksCommand())
        return Response(Status.OK)
    }
}