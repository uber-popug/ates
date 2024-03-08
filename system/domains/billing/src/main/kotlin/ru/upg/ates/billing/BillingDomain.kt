package ru.upg.ates.billing

import ru.upg.ates.AtesTopic
import ru.upg.ates.Domain
import ru.upg.ates.Topic
import ru.upg.ates.billing.table.BalanceChangeTable
import ru.upg.ates.billing.table.PaymentTable
import ru.upg.ates.billing.table.TaskTable
import ru.upg.ates.billing.table.UserTable
import ru.upg.ates.broker.EventsBroker
import ru.upg.ates.events.Event
import kotlin.reflect.KClass

class BillingDomain(
    override val broker: EventsBroker,
    val tables: Tables
) : Domain {

    override val notFoundTopic = AtesTopic.NOT_FOUND

    override val card: Map<KClass<out Event<*>>, Topic> = mapOf(
    )

    data class Tables(
        val users: UserTable,
        val tasks: TaskTable,
        val balanceChanges: BalanceChangeTable,
        val payments: PaymentTable,
    )
}
