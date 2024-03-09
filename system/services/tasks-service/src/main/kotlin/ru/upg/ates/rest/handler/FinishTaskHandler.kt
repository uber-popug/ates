package ru.upg.ates.rest.handler

import com.fasterxml.jackson.databind.ObjectMapper
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import ru.upg.ates.execute
import ru.upg.ates.tasks.TasksContext
import ru.upg.ates.tasks.command.FinishTask
import ru.upg.ates.tasks.model.Task

class FinishTaskHandler(
    private val mapper: ObjectMapper,
    private val domain: TasksContext,
) : HttpHandler {
    
    data class ResponsePayload(
        val task: Task
    )
    
    override fun invoke(request: Request): Response {
        val taskId = request.path("taskId")?.toLong() 
            ?: throw IllegalArgumentException("Wrong argument for taskId")
        
        val command = FinishTask(taskId)
        val result = domain.execute(command)

        val responsePayload = ResponsePayload(result)
        val responseContent = mapper.writeValueAsString(responsePayload)
        return Response(Status.OK).body(responseContent)
    }
}
