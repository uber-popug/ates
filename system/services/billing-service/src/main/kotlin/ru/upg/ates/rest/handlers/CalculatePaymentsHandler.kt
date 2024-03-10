package ru.upg.ates.rest.handlers

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.command.CalculatePayments
import ru.upg.ates.execute

class CalculatePaymentsHandler(
    private val context: BillingContext
):HttpHandler {
    override fun invoke(request: Request): Response {
        context.execute(CalculatePayments)
        return Response(Status.OK)
    }
}
