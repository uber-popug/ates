package ru.upg.ates.billing

import ru.upg.ates.AtesTopic
import ru.upg.ates.BoundedContext
import ru.upg.ates.Topic
import ru.upg.ates.billing.command.CalculatePayments
import ru.upg.ates.billing.command.ProcessTaskAssigned
import ru.upg.ates.billing.command.ProcessTaskFinished
import ru.upg.ates.billing.command.SaveTask
import ru.upg.ates.billing.command.SaveUser
import ru.upg.ates.broker.EventsBroker
import ru.upg.ates.events.BalanceChanged
import ru.upg.ates.events.EmailCreated
import ru.upg.ates.events.Event
import ru.upg.ates.events.PaymentCreated
import ru.upg.ates.events.TaskAssigned
import ru.upg.ates.events.TaskChanged
import ru.upg.ates.events.TaskFinished
import ru.upg.ates.events.UserChanged
import ru.upg.ates.execute
import ru.upg.ates.handler
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass


class BillingContext(val serviceName: String, override val broker: EventsBroker) : BoundedContext {

    override val notFoundTopic = AtesTopic.NOT_FOUND

    override val card: Map<KClass<out Event<*>>, Topic> = mapOf(
        BalanceChanged::class to AtesTopic.BALANCE_CHANGED,
        PaymentCreated::class to AtesTopic.PAYMENT_CREATED,
        EmailCreated::class to AtesTopic.EMAILS,
    )

    private val listener = broker.listener(serviceName)
        .register(AtesTopic.USERS, UserChanged::class, handler(::SaveUser))
        .register(AtesTopic.TASKS, TaskChanged::class, handler(::SaveTask))
        .register(AtesTopic.TASK_ASSIGNED, TaskAssigned::class, handler(::ProcessTaskAssigned))
        .register(AtesTopic.TASK_FINISHED, TaskFinished::class, handler(::ProcessTaskFinished))
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
