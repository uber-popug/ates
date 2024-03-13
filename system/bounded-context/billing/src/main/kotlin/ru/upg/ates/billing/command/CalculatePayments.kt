package ru.upg.ates.billing.command

import org.jetbrains.exposed.sql.transactions.transaction
import ru.upg.ates.Command
import ru.upg.ates.Topic
import ru.upg.ates.billing.BillingContext
import ru.upg.ates.billing.query.GetProfitableUsers
import ru.upg.ates.events.BalanceChangeReason
import ru.upg.ates.events.BalanceChanged
import ru.upg.ates.events.EmailCreated
import ru.upg.ates.execute

object CalculatePayments : Command.Silent<BillingContext> {
    private val reason = BalanceChangeReason.PAYMENT_SENT

    override fun execute(context: BillingContext) = with(context) {
        transaction {
            execute(GetProfitableUsers).forEach { (user, paymentAmount) ->
                val changeBalance = BalanceChanged(
                    userPid = user.pid,
                    taskPid = null,
                    reason = reason,
                    description = reason.description,
                    income = 0,
                    outcome = paymentAmount
                )

                execute(SaveBalanceChange(user, null, changeBalance))
                execute(SavePayment(user, changeBalance))

                publish(
                    Topic.EMAILS,
                    EmailCreated(
                        user.username,
                        reason.description,
                        "Направлена выплата в размере $paymentAmount"
                    )
                )
            }
        }
    }
}
