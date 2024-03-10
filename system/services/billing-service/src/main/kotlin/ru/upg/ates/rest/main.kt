package ru.upg.ates.rest

import org.http4k.server.Undertow
import org.http4k.server.asServer
import ru.upg.ates.AtesInfra
import ru.upg.ates.InfraConfig
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.table.BalanceChangeTable
import ru.upg.ates.billing.table.PaymentTable
import ru.upg.ates.billing.table.TaskTable
import ru.upg.ates.billing.table.UserTable
import ru.upg.ates.rest.handlers.CalculatePaymentsHandler
import ru.upg.ates.rest.handlers.GetAdminProfitHandler
import ru.upg.ates.rest.handlers.GetUserAccountHandler

fun main() {
    val config = InfraConfig.local
    val dbConfig = config.databases.billing
    val serviceConfig = config.services.billing

    val infra = AtesInfra(config).apply {
        initDatabase(dbConfig, UserTable, TaskTable, BalanceChangeTable, PaymentTable)
    }

    val context = BillingContext(serviceConfig.name, infra.kafka)

    val app = infra.billingService(
        AtesInfra.BillingHandlers(
            getUserAccount = GetUserAccountHandler(infra.httpMapper, context),
            getAdminsProfit = GetAdminProfitHandler(infra.httpMapper, context),
            calculatePayments = CalculatePaymentsHandler(context)
        )
    )

    app.asServer(Undertow(serviceConfig.port)).start()
}
