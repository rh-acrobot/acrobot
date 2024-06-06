package com.redhat.acrobot

import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import org.hibernate.cfg.AvailableSettings
import kotlin.system.exitProcess

private fun requireEnv(env: String, description: String): String {
    val result = System.getenv(env)
    if (result != null) return result

    System.err.println("Missing required environment variable $env: $description")
    exitProcess(1)
}

private fun makeOverrides(): Map<String, String> {
    return mapOf(
        AvailableSettings.JAKARTA_JDBC_URL to requireEnv(
            "ACROBOT_DB_URL",
            "the JDBC URL used for the database connection",
        ),
        AvailableSettings.JAKARTA_JDBC_USER to requireEnv(
            "ACROBOT_DB_USER",
            "the username used for the database connection",
        ),
        AvailableSettings.JAKARTA_JDBC_PASSWORD to requireEnv(
            "ACROBOT_DB_PASS",
            "the password used for the database connection",
        ),
    )
}

fun createEntityManagerFactory(): EntityManagerFactory {
    return Persistence.createEntityManagerFactory("com.redhat.acrobot", makeOverrides())
}