package ru.upg.ates.tasks.model

import java.util.*

data class Task(
    val id: Long,
    val pid: UUID,
    val name: String,
    val price: Int,
    val finished: Boolean
)