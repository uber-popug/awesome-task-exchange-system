package ru.upg.ates.auth

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import ru.upg.ates.auth.operation.RegisterUser
import ru.upg.ates.auth.table.UserTable
import ru.upg.ates.events.Role
import ru.upg.ates.events.broker.KafkaEventsBroker
import ru.upg.ates.execute
import ru.upg.ates.schema.LoadJsonSchemas

class RegisterUserTest {

    @Test
    fun test() {
        val schemasPath = "C:\\dev\\reps\\uber-popug\\awesome-task-exchange-system\\system\\events\\events-schema-registry\\schemas"
        val kafkaUrl = "http://localhost:9994"
        val schemas = LoadJsonSchemas(schemasPath).execute()
        val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())

        val broker = KafkaEventsBroker(kafkaUrl, schemas, mapper)
        val database = Database.connect(
            url = "jdbc:postgresql://localhost:5432/ates_auth",
            user = "postgres",
            password = "postgres"
        )

        val domain = AuthDomain(
            broker = broker,
            database = database,
            tables = AuthDomain.Tables(
                users = UserTable
            )
        )

        transaction {
            SchemaUtils.createMissingTablesAndColumns(domain.tables.users)
        }

        domain.execute(
            RegisterUser(
                role = Role.WORKER,
                username = "Worker ${System.currentTimeMillis()}",
                password = "123456"
            )
        )
    }
}
