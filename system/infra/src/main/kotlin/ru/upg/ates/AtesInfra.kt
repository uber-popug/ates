package ru.upg.ates

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.broker.KafkaEventsBroker
import ru.upg.ates.schema.LoadJsonSchemas

class AtesInfra(private val config: InfraConfig) {
    val eventsMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    
    val httpMapper = eventsMapper
    
    val kafka by lazy { 
        KafkaEventsBroker(
            url = config.kafkaUrl,
            jsonSchemas = LoadJsonSchemas(config.schemasPath).execute(),
            mapper = eventsMapper
        )
    }
    
    
    data class TasksHandlers(
        val listTasks: HttpHandler,
        val createTask: HttpHandler,
        val reassignTasks: HttpHandler,
        val finishTask: HttpHandler
    )
    
    val taskService = { handlers: TasksHandlers ->
        routes(
            "/ping" bind Method.GET to { Response(Status.OK).body("pong") },
            "/tasks" bind routes(
                Method.GET bind handlers.listTasks,
                Method.POST bind handlers.createTask,
                "/reassign" bind Method.POST to handlers.reassignTasks,
                "/{taskId}/finish" bind Method.POST to handlers.finishTask
            )
        )
    }


    class BillingHandlers(
        val getUserAccount: HttpHandler,
        val getAdminsProfit: HttpHandler,
        val calculatePayments: HttpHandler
    )
    
    val billingService = { handlers: BillingHandlers ->
        routes(
            "/ping" bind Method.GET to { Response(Status.OK).body("pong") },
            "/billing" bind routes(
                "/accounts/{userPid}" bind Method.GET to handlers.getUserAccount,
                "/admins-profit" bind Method.GET to handlers.getAdminsProfit,
                "/payments/calculate" bind Method.POST to handlers.calculatePayments
            )
        )
    } 
    
    
    class AnalyticHandlers(
        val getAnalyticHandler: HttpHandler
    )
    
    val analyticService = { handlers: AnalyticHandlers -> 
        routes(
            "/ping" bind Method.GET to { Response(Status.OK).body("pong") },
            "/analytic" bind Method.GET to handlers.getAnalyticHandler
        )
    }
    
    fun initDatabase(dbConfig: InfraConfig.Db, vararg tables: Table) {
        dbConfig.run {
            Database.connect(
                url = url,
                user = username,
                password = password
            )
            
            transaction {
                SchemaUtils.createMissingTablesAndColumns(*tables)
            }
        }
    }
}
