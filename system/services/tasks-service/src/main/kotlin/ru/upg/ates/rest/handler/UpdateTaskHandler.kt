package ru.upg.ates.rest.handler

import com.fasterxml.jackson.databind.ObjectMapper
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.routing.path
import ru.upg.ates.tasks.TasksDomain

class UpdateTaskHandler(
    private val mapper: ObjectMapper,
    private val domain: TasksDomain
): HttpHandler {

    override fun invoke(request: Request): Response {
        val id = request.path("id")?.toLong()
            ?: throw IllegalArgumentException("Missing id")

        TODO()
    }
}