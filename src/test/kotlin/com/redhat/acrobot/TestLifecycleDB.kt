package com.redhat.acrobot

import jakarta.persistence.Persistence
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.AvailableSettings
import org.hibernate.tool.schema.Action
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
interface TestLifecycleDB {
    private companion object {
        lateinit var sessionFactory: SessionFactory
        lateinit var session: Session
    }

    @BeforeAll
    fun setUpSuite() {
        sessionFactory = Persistence
            .createEntityManagerFactory(
                "com.redhat.acrobot", mapOf(
                    AvailableSettings.JAKARTA_JDBC_URL to "jdbc:h2:mem:acrobot_test",
                    AvailableSettings.JAKARTA_JDBC_USER to "acrobot",
                    AvailableSettings.JAKARTA_JDBC_PASSWORD to "",
                    AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION to Action.SPEC_ACTION_DROP_AND_CREATE,
                )
            )
            .unwrap(SessionFactory::class.java)
    }

    @AfterAll
    fun tearDownSuite() {
        sessionFactory.close()
    }

    @BeforeEach
    fun setUp() {
        sessionFactory.schemaManager.truncateMappedObjects()
        Companion.session = sessionFactory.openSession()

        session.transaction.begin()
    }

    @AfterEach
    fun tearDown() {
        session.transaction.rollback()
        session.close()
    }

    val session
        get() = Companion.session
}
