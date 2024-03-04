package ru.upg.ates.auth

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import ru.upg.ates.auth.operation.RegisterUser
import ru.upg.ates.auth.table.UserTable
import ru.upg.ates.event.Role
import ru.upg.common.ddd.execute

class RegisterUserTest {

    @Test
    fun test() {
        val domain = AuthDomain(
            kafkaUrl = "http://localhost:9994",
            tables = AuthDomain.Tables(
                userTable = UserTable
            )
        )

        Database.connect(
            url = "jdbc:postgresql://localhost:5432/ates_auth",
            user = "postgres",
            password = "postgres"
        )

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                domain.tables.userTable
            )
        }

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