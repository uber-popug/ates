package ru.upg.ates.billing.model

import java.time.LocalDate

data class DayProfit(
    val day: LocalDate, 
    val profit: Long
)
