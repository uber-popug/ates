package ru.upg.ates.tasks.commands

import org.slf4j.LoggerFactory
import ru.upg.ates.event.UserCUD
import ru.upg.ates.tasks.dao.UsersDao

class SaveUserChange(
    private val users: UsersDao
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun save(event: UserCUD) {
        when(event) {
            is UserCUD.Created -> users.create(event.user)
            is UserCUD.Updated -> users.update(event.user)
            else -> {
                log.warn("not handled UserCUD event ${event.javaClass}")
            }
        }
    }
}