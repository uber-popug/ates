package ru.upg.ates.rest.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.routing.path
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.query.GetUserBalance
import ru.upg.ates.execute
import java.time.Instant

class GetUserAccountHandler(
    private val mapper: ObjectMapper,
    private val context: BillingContext
) : HttpHandler {
    
    data class ResponsePayload(
        val balance: Long,
        val history: List<GetUserBalance>
    ) {
        data class Task(
            val id: Long,
            val title: String
        )
        data class History(
            val id: Long,
            val timestamp: Instant,
            val amount: Long,
            val description: String,
            val task: Task
        )
    }
    
    override fun invoke(request: Request): Response {
        val userId = request.path("userId")?.toLong() ?: throw IllegalArgumentException(
            "Not found path parameter `userId`"
        )
        
        val balance = context.execute(GetUserBalance(userId))
        
    }
}
