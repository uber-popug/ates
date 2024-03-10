package ru.upg.ates.billing

import ru.upg.ates.BoundedContext
import ru.upg.ates.Topic
import ru.upg.ates.billing.command.CalculatePayments
import ru.upg.ates.billing.command.ProcessTaskAssigned
import ru.upg.ates.billing.command.ProcessTaskFinished
import ru.upg.ates.billing.command.SaveTask
import ru.upg.ates.billing.command.SaveUser
import ru.upg.ates.broker.EventsBroker
import ru.upg.ates.events.TaskAssigned
import ru.upg.ates.events.TaskCreatedV1
import ru.upg.ates.events.TaskFinished
import ru.upg.ates.events.UserCreated
import ru.upg.ates.execute
import ru.upg.ates.handler
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class BillingContext(
    override val serviceName: String,
    override val broker: EventsBroker
) : BoundedContext {

    private val listener = broker.listener(serviceName)
        .register(Topic.USERS, UserCreated::class, handler(::SaveUser))
        .register(Topic.TASKS, TaskCreatedV1::class, handler(::SaveTask))
        .register(Topic.TASK_ASSIGNED, TaskAssigned::class, handler(::ProcessTaskAssigned))
        .register(Topic.TASK_FINISHED, TaskFinished::class, handler(::ProcessTaskFinished))
        .listen()

    private val scheduler = run {
        val secondsToEndOfTheDay: Long = Duration.between(
            LocalDateTime.now(),
            LocalDate.now().plusDays(1).atStartOfDay()
        ).toSeconds()

        val secondsInDay: Long = 3600 * 24

        val calc = Runnable { execute(CalculatePayments) }
        Executors.newSingleThreadScheduledExecutor().apply {
            scheduleAtFixedRate(calc, secondsToEndOfTheDay, secondsInDay, TimeUnit.SECONDS)
        }
    }
}
