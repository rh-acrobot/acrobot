package com.redhat.acrobot

import com.redhat.acrobot.entities.Acronym
import com.redhat.acrobot.entities.Explanation
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandTest : TestLifecycleDB {
    private inline fun <reified T> getAllOf(): List<T> {
        return session.createQuery(
            session.criteriaBuilder.createQuery(T::class.java).also { query ->
                query.select(query.from(T::class.java))
            },
        ).resultList
    }

    private fun getAllAcronyms(): List<Acronym> {
        return getAllOf<Acronym>()
    }

    private fun getAllExplanations(): List<Explanation> {
        return getAllOf<Explanation>()
    }

    private val userA = "USER_A"
    private val userB = "USER_B"

    private fun runCommand(command: String, user: String = userA): String {
        return processCommand(userId = user, session = session, command = command)
    }

    private fun assertOutput(expected: String, command: String, user: String = userA) {
        assertEquals(expected, runCommand(command = command, user = user))
    }

    private fun addExplanation(acronym: String, explanation: String) {
        assertOutput(
            Messages.EXPLANATION_SAVED,
            "${CommandFormat.CHANGE_PREFIX}${acronym} ${CommandFormat.ACRONYM_SEPARATOR} $explanation",
        )
    }

    @Test
    fun `acronym does not exist`() {
        assertOutput(Messages.ACRONYM_NOT_FOUND, "TEST")
    }

    @Test
    fun `create and read single explanation`() {
        addExplanation("TEST", "An explanation.")
        assertOutput("An explanation.", "TEST")
    }

    @Test
    fun `create and read multiple explanations`() {
        addExplanation("TEST", "Z0")
        addExplanation("TEST", "a0")
        addExplanation("TEST", "z1")
        addExplanation("TEST", "A1")

        // Check for case-insensitive alphabetical order
        assertOutput("a0\nA1\nZ0\nz1", "TEST")
    }

    @Test
    fun `update explanation`() {
        addExplanation("TEST", "Hello!")
        assertOutput("Hello!", "TEST")

        assertOutput(Messages.ACRONYM_UPDATED, "!TEST = Hello! => Goodbye!")
        assertOutput("Goodbye!", "TEST")
    }

    @Test
    fun `delete explanation`() {
        addExplanation("TEST", "")
    }
}
