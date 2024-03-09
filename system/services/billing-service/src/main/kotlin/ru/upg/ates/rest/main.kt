package ru.upg.ates.rest

import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.server.Undertow
import org.http4k.server.asServer
import ru.upg.ates.AtesInfra
import ru.upg.ates.InfraConfig
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.table.BalanceChangeTable
import ru.upg.ates.billing.table.PaymentTable
import ru.upg.ates.billing.table.TaskTable
import ru.upg.ates.billing.table.UserTable

fun main() {
    val config = InfraConfig.local
    val dbConfig = config.databases.billing
    val serviceConfig = config.services.billing

    val infra = AtesInfra(config).apply {
        initDatabase(dbConfig, UserTable, TaskTable, BalanceChangeTable, PaymentTable)
    }

    val domain = BillingContext(serviceConfig.name, infra.kafka)

    val app = infra.billingService(
        AtesInfra.BillingHandlers(
            {Response(Status.OK)},
            {Response(Status.OK)},
            {Response(Status.OK)},
        )
    )

    app.asServer(Undertow(serviceConfig.port)).start()
}
