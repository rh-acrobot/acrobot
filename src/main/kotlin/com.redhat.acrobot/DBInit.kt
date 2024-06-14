package com.redhat.acrobot

import jakarta.persistence.Persistence
import org.flywaydb.core.Flyway
import org.hibernate.SessionFactory
import org.hibernate.cfg.AvailableSettings
import kotlin.system.exitProcess

private fun requireEnv(env: String, description: String): String {
    val result = System.getenv(env)
    if (result != null) return result

    System.err.println("Missing required environment variable $env: $description")
    exitProcess(1)
}

fun createSessionFactory(): SessionFactory {
    val url = requireEnv(
        "ACROBOT_DB_URL",
        "the JDBC URL used for the database connection",
    )
    val user = requireEnv(
        "ACROBOT_DB_USER",
        "the username used for the database connection",
    )

    val password = requireEnv(
        "ACROBOT_DB_PASS",
        "the password used for the database connection",
    )

    val flyway = Flyway.configure().dataSource(url, user, password).load()
    flyway.migrate()

    return Persistence
        .createEntityManagerFactory(
            "com.redhat.acrobot", mapOf(
                AvailableSettings.JAKARTA_JDBC_URL to url,
                AvailableSettings.JAKARTA_JDBC_USER to user,
                AvailableSettings.JAKARTA_JDBC_PASSWORD to password,
            )
        )
        .unwrap(SessionFactory::class.java)
}
