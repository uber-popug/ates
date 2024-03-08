package ru.upg.ates.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.core.Method
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.upg.ates.rest.handler.CreateTaskHandler
import ru.upg.ates.tasks.TasksContext
import ru.upg.ates.tasks.table.TaskTable
import ru.upg.ates.tasks.table.UserTable

class TasksServiceTest {

    @Test
    fun test() {
        val tables = TasksContext.Tables(
            TaskTable,
            UserTable
        )

        val domain = TasksContext(tables, TasksContext.Config(
            kafkaUrl = "http://localhost:9994",
            db = TasksContext.Config.Db(
                url = "jdbc:postgresql://localhost:5432/ates_tasks",
                username = "postgres",
                password = "postgres"
            )
        ))

        val app = tasksServiceApp(domain)

        val taskName = "Test task"
        val mapper = jacksonObjectMapper()
        val requestPayload = CreateTaskHandler.RequestPayload(taskName)
        val requestBody = mapper.writeValueAsString(requestPayload)

        val response = app(org.http4k.core.Request(Method.POST, "/tasks").body(requestBody))
        val responseContent = response.bodyString()
        val responsePayload = mapper.readValue<CreateTaskHandler.ResponsePayload>(responseContent)
        Assertions.assertEquals(taskName, responsePayload.task.name)
        Assertions.assertEquals(false, responsePayload.task.finished)
    }
}
