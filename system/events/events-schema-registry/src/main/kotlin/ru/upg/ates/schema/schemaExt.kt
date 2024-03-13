package ru.upg.ates.schema

import com.networknt.schema.InputFormat
import com.networknt.schema.JsonSchema

fun JsonSchema.validate(eventContent: String, msgPrefix: String = "") {
    val messages = validate(eventContent, InputFormat.JSON)
    if (messages.isNotEmpty()) {
        val msg = messages.joinToString { it.message }
        throw IllegalArgumentException(
            "event content not corresponding schema ${this.id} " +
            "with validation message '$msg'"
        )
    }
}
