package ru.upg.ates.auth

import org.junit.jupiter.api.Test
import ru.upg.ates.AtesInfra
import ru.upg.ates.InfraConfig
import ru.upg.ates.auth.operation.RegisterUser
import ru.upg.ates.auth.table.UserTable
import ru.upg.ates.events.Role
import ru.upg.ates.execute

class RegisterUserTest {

    @Test
    fun test() {
        val config = InfraConfig.local
        val serviceConfig = config.services.auth
        val dbConfig = config.databases.auth

        val infra = AtesInfra(config).apply {
            initDatabase(dbConfig, UserTable)
        }

        AuthContext(serviceConfig.name, infra.kafka).apply {
            execute(
                RegisterUser(
                    role = Role.WORKER,
                    username = "Worker ${System.currentTimeMillis()}",
                    password = "123456"
                )
            )
        }
    }
}
