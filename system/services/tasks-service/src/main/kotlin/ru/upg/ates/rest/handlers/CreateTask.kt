package ru.upg.ates.rest.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.model.Task
import ru.upg.ates.tasks.commands.CreateTaskCommand
import ru.upg.common.ddd.execute


class CreateTaskHandler(
    private val mapper: ObjectMapper,
    private val domain: TasksDomain
): HttpHandler {

    private data class RequestPayload(
        val name: String
    )

    private data class ResponsePayload(
        val task: Task
    )

    override fun invoke(request: Request): Response {
        val payload = mapper.readValue<RequestPayload>(request.bodyString())
        val aggregate = CreateTaskCommand.Aggregate(payload.name)
        val command = CreateTaskCommand(aggregate)
        domain.execute(command)
        return Response(Status.OK)
    }
}