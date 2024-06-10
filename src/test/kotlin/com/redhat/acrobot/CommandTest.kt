package com.redhat.acrobot

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandTest : TestLifecycleDB {
    private val userA = "USER_A"
    private val userB = "USER_B"

    private fun runCommand(command: String, user: String = userA): String {
        return processCommand(userId = user, session = session, command = command)
    }

    private fun assertOutput(expected: String, command: String, user: String = userA) {
        assertEquals(expected, runCommand(command = command, user = user))
    }

    private fun addExplanation(acronym: String, explanation: String, user: String = userA) {
        assertOutput(
            Messages.EXPLANATION_SAVED,
            "${CommandFormat.CHANGE_PREFIX}${acronym} ${CommandFormat.ACRONYM_SEPARATOR} $explanation",
            user = user,
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

        assertOutput(Messages.EXPLANATION_UPDATED, "!TEST = Hello! => Goodbye!")
        assertOutput("Goodbye!", "TEST")
    }

    @Test
    fun `delete explanation`() {
        addExplanation("TEST", "An explanation.")
        assertOutput(Messages.EXPLANATION_REMOVED, "!TEST = An explanation. => ");
        assertOutput(Messages.ACRONYM_NOT_FOUND, "TEST")
    }

    @Test
    fun `update nonexistent explanation`() {
        addExplanation("TEST", "Hello!")
        assertOutput(Messages.EXPLANATION_NOT_FOUND, "!TEST = Goodbye => *waves*")
        assertOutput("Hello!", "TEST")
    }

    @Test
    fun `remove nonexistent explanation`() {
        addExplanation("TEST", "Hello!")
        assertOutput(Messages.EXPLANATION_NOT_FOUND, "!TEST = Goodbye => ")
        assertOutput("Hello!", "TEST")
    }

    @Test
    fun `add duplicate explanation`() {
        addExplanation("TEST", "Hello!")

        assertOutput(
            "That explanation already exists for the given acronym. If you created it, you can update it with !change TEST = Hello! => [new version]",
            "!TEST = Hello!",
        )
    }

    @Test
    fun `multiple separators`() {
        assertOutput(Messages.MULTIPLE_UPDATE_SEPARATORS, "!TEST = a => b => c")
        assertOutput(Messages.ACRONYM_NOT_FOUND, "TEST")
    }

    @Test
    fun `retrieval is case-insensitive`() {
        addExplanation("TEST", "An explanation.")
        assertOutput("An explanation.", "Test")
    }

    @Test
    fun `updating can only be done by same user`() {
        addExplanation("TEST", "An explanation.", user = userA)
        assertOutput(Messages.INSUFFICIENT_PRIVILEGES, "!TEST = An explanation. => Something else", user = userB)
        assertOutput("An explanation.", "TEST")
    }

    @Test
    fun `removing can only be done by same user`() {
        addExplanation("TEST", "An explanation.", user = userA)
        assertOutput(Messages.INSUFFICIENT_PRIVILEGES, "!TEST = An explanation. => ", user = userB)
        assertOutput("An explanation.", "TEST")
    }

    @Test
    fun `update nonexistent acronym`() {
        addExplanation("TEST", "An explanation.")
        assertOutput(Messages.ACRONYM_NOT_FOUND, "!FOO = An explanation. => Something else")
        assertOutput("An explanation.", "TEST")
    }

    @Test
    fun `remove nonexistent acronym`() {
        addExplanation("TEST", "An explanation.")
        assertOutput(Messages.ACRONYM_NOT_FOUND, "!FOO = An explanation. => ")
        assertOutput("An explanation.", "TEST")
    }

    @ParameterizedTest
    @ValueSource(strings = ["!FOO", "!FOO => a", "!FOO => a => a"])
    fun `invalid update syntax`(command: String) {
        assertOutput(Messages.INCORRECT_FORMAT_FOR_SAVING_ACRONYM, command)
    }
}
