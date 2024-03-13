package ru.upg.ates.http.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import ru.upg.ates.analytic.AnalyticContext
import ru.upg.ates.analytic.model.Task
import ru.upg.ates.analytic.query.GetAmountNegativeBalances
import ru.upg.ates.analytic.query.GetMostExpensiveTask
import ru.upg.ates.analytic.query.GetTodayProfit
import ru.upg.ates.execute
import java.time.LocalDate

class GetAnalyticHandler(
    private val mapper: ObjectMapper,
    private val context: AnalyticContext
) : HttpHandler {
    
    data class ResponsePayload(
        val todayProfit: Long,
        val amountNegative: Long,
        val today: MostExpensive,
        val week: MostExpensive,
        val month: MostExpensive
    ) {
        data class MostExpensive(
            val from: LocalDate,
            val to: LocalDate,
            val task: Task?
        )
    }
    
    
    override fun invoke(request: Request): Response {
        val todayProfit = context.execute(GetTodayProfit)
        val amountNegative = context.execute(GetAmountNegativeBalances)
        
        val today = LocalDate.now()
        val todayMostExpensive = context.execute(GetMostExpensiveTask(today, today))
        
        val monday = today.minusDays(today.dayOfWeek.value - 1L)
        val sunday = monday.plusDays(6)
        val weekMostExpensive = context.execute(GetMostExpensiveTask(monday, sunday))
        
        val monthFirstDay = today.minusDays(today.dayOfMonth - 1L)
        val monthEndDay = monthFirstDay.plusMonths(1).minusDays(1)
        val monthMostExpensive = context.execute(GetMostExpensiveTask(monthFirstDay, monthEndDay))
        
        val payload = ResponsePayload(
            todayProfit = todayProfit,
            amountNegative = amountNegative,
            today = ResponsePayload.MostExpensive(today, today, todayMostExpensive),
            week = ResponsePayload.MostExpensive(monday, sunday, weekMostExpensive),
            month = ResponsePayload.MostExpensive(monthFirstDay, monthEndDay, monthMostExpensive)
        )
        
        val responseContent = mapper.writeValueAsString(payload)
        return Response(Status.OK).body(responseContent)
    }
}
