package ru.upg.ates

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
import ru.upg.ates.broker.KafkaEventsBroker
import ru.upg.ates.schema.LoadJsonSchemas

class AtesInfra(val config: InfraConfig) {
    val eventsMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    
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
        println("auth database connected ${tasksDatabase.vendor}")
        
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
    
    val authDatabase by lazy {
        config.databases.auth.run {
            Database.connect(url, username, password)    
        }
    }

    val tasksDatabase by lazy {
        config.databases.tasks.run {
            Database.connect(url, username, password)
        }
    }

    val billingDatabase by lazy {
        config.databases.billing.run {
            Database.connect(url, username, password)
        }
    }

    val analyticDatabase by lazy {
        config.databases.analytic.run {
            Database.connect(url, username, password)
        }
    }
}
