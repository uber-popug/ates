package ru.upg.ates.tasks.operation

import ru.upg.ates.tasks.dao.UsersDao
import java.util.*

class GetRandomWorkers(
    private val users: UsersDao
) {

    operator fun invoke(amount: Int): List<UUID> {
        val workers = users.getWorkers()
        return mutableListOf<UUID>().also { users ->
            repeat(amount) {
                val randomWorker = (0..workers.size).random()
                users += workers[randomWorker].id
            }
        }
    }
}