package ru.upg.ates.tasks.operation

import ru.upg.ates.tasks.dao.TasksDao
import ru.upg.ates.tasks.model.Task
import java.util.*

class CreateTask(
    private val getRandomWorkersOperation: GetRandomWorkers,
    private val tasks: TasksDao
) {

    operator fun invoke() {
        val task = Task(
            id = UUID.randomUUID(),
            name = "Task ${System.currentTimeMillis()}",
            userId = getRandomWorkersOperation(1).first()
        )

        tasks.create(task)


    }
}