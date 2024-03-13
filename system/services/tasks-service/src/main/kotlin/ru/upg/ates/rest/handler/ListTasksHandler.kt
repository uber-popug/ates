package ru.upg.ates.rest.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.queries
import ru.upg.ates.execute
import ru.upg.ates.tasks.TasksContext
import ru.upg.ates.tasks.query.ListTasks

class ListTasksHandler(
    private val mapper: ObjectMapper,
    private val domain: TasksContext,
) : HttpHandler {

    data class RequestPayload(
        val showFinished: Boolean = false,
        val search: String?,
        val user: Long?,
        val page: Long = 1,
        val pageSize: Int = 25
    )

    override fun invoke(request: Request): Response {
        val queriesContent = mapper.writeValueAsString(request.uri.queries().toMap())
        val requestPayload = mapper.readValue<RequestPayload>(queriesContent)
        val query = requestPayload.run {
            ListTasks(
                showFinished = showFinished,
                search = search,
                userId = user,
                page = page,
                pageSize = pageSize
            )
        }

        val result = domain.execute(query)
        val responseContent = mapper.writeValueAsString(result)
        return Response(Status.OK).body(responseContent)
    }
}
