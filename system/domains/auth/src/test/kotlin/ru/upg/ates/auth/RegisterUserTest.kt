package ru.upg.ates.auth

import org.junit.jupiter.api.Test
import ru.upg.ates.auth.operation.RegisterUser
import ru.upg.ates.auth.table.UserTable
import ru.upg.ates.events.Role
import ru.upg.ates.model.DomainConfig
import ru.upg.ates.ddd.execute

class RegisterUserTest {

    @Test
    fun test() {
        val domain = AuthDomain(
            tables = AuthDomain.Tables(
                users = UserTable
            ),
            config = DomainConfig(
                kafkaUrl = "http://localhost:9994",
                db = DomainConfig.Db(
                    url = "jdbc:postgresql://localhost:5432/ates_auth",
                    username = "postgres",
                    password = "postgres"
                )
            )
        )

        domain.execute(
            RegisterUser(
                RegisterUser.Aggregate(
                    role = Role.WORKER,
                    username = "Worker ${System.currentTimeMillis()}",
                    password = "123456"
                )
            )
        )
    }
}