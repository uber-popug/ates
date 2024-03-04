package ru.upg.ates.operation

import ru.upg.ates.AtesTopic

class ListenEvents(
    private val kafkaServer: String,
    private val consumerGroup: String
) {

    fun <T : Any> listen(topic: AtesTopic, handler: (T) -> Unit): ListenEvents {
        // TODO()
        return this
    }
}