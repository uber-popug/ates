package ru.upg.ates.http

import org.http4k.server.Undertow
import org.http4k.server.asServer
import ru.upg.ates.AtesInfra
import ru.upg.ates.InfraConfig
import ru.upg.ates.analytic.AnalyticContext
import ru.upg.ates.analytic.table.BalanceChangeTable
import ru.upg.ates.analytic.table.TaskTable
import ru.upg.ates.analytic.table.UserTable
import ru.upg.ates.http.handlers.GetAnalyticHandler

fun main() {
    val config = InfraConfig.local
    val dbConfig = config.databases.analytic
    val serviceConfig = config.services.analytic

    val infra = AtesInfra(config).apply {
        initDatabase(dbConfig, UserTable, TaskTable, BalanceChangeTable)
    }

    val context = AnalyticContext(serviceConfig.name, infra.kafka)

    val app = infra.analyticService(
        AtesInfra.AnalyticHandlers(
            getAnalyticHandler = GetAnalyticHandler(infra.httpMapper, context),
        )
    )

    app.asServer(Undertow(serviceConfig.port)).start()
}
