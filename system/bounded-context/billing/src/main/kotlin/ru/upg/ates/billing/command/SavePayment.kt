package ru.upg.ates.billing.command

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.Topic
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.model.User
import ru.upg.ates.billing.table.PaymentTable
import ru.upg.ates.events.BalanceChanged
import ru.upg.ates.events.PaymentCreated
import ru.upg.ates.events.PaymentStatus
import java.time.Instant
import java.util.*

class SavePayment(
    private val user: User,
    private val change: BalanceChanged,
) : Command.Silent<BillingContext> {

    override fun execute(context: BillingContext) {
        transaction {
            val paymentPid = UUID.randomUUID()
            PaymentTable.insert {
                it[pid] = paymentPid
                it[userId] = user.id
                it[description] = change.description
                it[amount] = change.outcome
                it[status] = PaymentStatus.CREATED
                it[createdAt] = Instant.now()
            }

            PaymentCreated(paymentPid, user.pid, change.description, change.outcome).let {
                context.publish(Topic.PAYMENTS, it)    
                context.publish(Topic.PAYMENT_CREATED, it)    
            }
        }
    }
}
