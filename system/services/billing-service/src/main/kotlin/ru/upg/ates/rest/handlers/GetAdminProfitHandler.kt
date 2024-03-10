package ru.upg.ates.rest.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.query.ListProfit
import ru.upg.ates.execute
import java.time.LocalDate

class GetAdminProfitHandler(
    private val mapper: ObjectMapper,
    private val context: BillingContext
): HttpHandler {
    
    override fun invoke(request: Request): Response {
        val from = request.getLocalDate("from")
        val to = request.getLocalDate("to")
        val profit = context.execute(ListProfit(from, to))
        val responseContent = mapper.writeValueAsString(profit)
        return Response(Status.OK).body(responseContent)
    }
    
    private fun Request.getLocalDate(name: String) = 
        query(name)?.let(LocalDate::parse)
            ?: throw IllegalArgumentException(
                "Not found required '$name' query") 
}
