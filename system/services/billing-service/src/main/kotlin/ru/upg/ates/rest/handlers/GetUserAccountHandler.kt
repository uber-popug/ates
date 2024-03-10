package ru.upg.ates.rest.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.model.BalanceChange
import ru.upg.ates.billing.query.GetUserBalance
import ru.upg.ates.billing.query.ListBalanceChanges
import ru.upg.ates.execute
import java.time.Instant
import java.util.UUID

class GetUserAccountHandler(
    private val mapper: ObjectMapper,
    private val context: BillingContext
) : HttpHandler {

    data class ResponsePayload(
        val balance: Long,
        val history: List<History>
    ) {
        data class Task(
            val id: Long,
            val title: String
        )

        data class History(
            val id: Long,
            val timestamp: Instant,
            val income: Long,
            val outcome: Long,
            val description: String,
            val task: Task?
        )
    }

    override fun invoke(request: Request): Response {
        val userId = request.path("userPid")?.let(UUID::fromString) ?: throw IllegalArgumentException(
            "Not found path parameter `userId`"
        )

        val balance = context.execute(GetUserBalance(userId))
        val changes = context.execute(ListBalanceChanges(userId))
        val responsePayload = buildResponsePayload(balance, changes)
        val responseContent = mapper.writeValueAsString(responsePayload)
        return Response(Status.OK).body(responseContent)
    }

    private fun buildResponsePayload(
        balance: Long,
        changes: List<BalanceChange>
    ) = ResponsePayload(
        balance = balance,
        history = changes.map { change ->
            ResponsePayload.History(
                id = change.id,
                timestamp = change.timestamp,
                income = change.income,
                outcome = change.outcome,
                description = change.description,
                task = change.task?.let { task ->
                    ResponsePayload.Task(
                        id = task.id,
                        title = task.title
                    )
                }
            )
        }
    )
}
