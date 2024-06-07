package com.redhat.acrobot

import com.redhat.acrobot.entities.Acronym
import com.redhat.acrobot.entities.Explanation
import org.junit.jupiter.api.Nested
import kotlin.test.*

class EntitiesTest : TestLifecycleDB {
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

    @Nested
    inner class `acronym tests` {
        @Test
        fun `findAcronym on empty is null`() {
            assertNull(findAcronym(session, "TEST"))
        }

        @Test
        fun `findOrCreateAcronym creates acronym`() {
            val created = findOrCreateAcronym(session, "TEST")
            val stored = getAllAcronyms()

            assertEquals(1, stored.count())
            assertEquals(created, stored.single())
        }

        @Test
        fun `findAcronym can find created acronym`() {
            val created = findOrCreateAcronym(session, "TEST")
            val found = findAcronym(session, "TEST")

            assertEquals(created, found)
        }

        @Test
        fun `findOrCreateAcronym can find created acronym`() {
            val created = findOrCreateAcronym(session, "TEST")
            val found = findOrCreateAcronym(session, "TEST")

            assertEquals(created, found)
            assertEquals(1, getAllAcronyms().count())
        }

        @Test
        fun `findAcronym ignores casing`() {
            val created = findOrCreateAcronym(session, "TEST")
            val found = findAcronym(session, "test")

            assertEquals(created, found)
        }

        @Test
        fun `findOrCreateAcronym ignores casing`() {
            val created = findOrCreateAcronym(session, "TEST")
            val found = findOrCreateAcronym(session, "test")

            assertEquals(created, found)
            assertEquals(1, getAllAcronyms().count())
        }
    }

    @Nested
    inner class `explanation tests` {
        private lateinit var acronymA: Acronym
        private lateinit var acronymB: Acronym

        private val user = "USER"

        @BeforeTest
        fun setUp() {
            acronymA = findOrCreateAcronym(session, "A")
            acronymB = findOrCreateAcronym(session, "B")
        }

        @Test
        fun `findExplanation returns null when no explanations exist`() {
            assertNull(findExplanation(session, acronymA, "An explanation."))
        }

        @Test
        fun `findExplanation finds explanation when it exists`() {
            val text = "An explanation."

            val created = acronymA.createExplanation(user, text)
            session.persist(created)

            val found = findExplanation(session, acronymA, text)

            assertEquals(created, found)
        }

        @Test
        fun `findExplanation does not find explanation for other acronym`() {
            val text = "An explanation."

            val created = acronymA.createExplanation(user, text)
            session.persist(created)

            assertNull(findExplanation(session, acronymB, text))
        }

        @Test
        fun `findExplanation does not find explanation for different text`() {
            val created = acronymA.createExplanation(user, "An explanation.")
            session.persist(created)

            assertNull(findExplanation(session, acronymA, "Some other explanation."))
        }

        @Test
        fun `findExplanation handles explanations with the same text`() {
            val text = "An explanation."

            val createdA = acronymA.createExplanation(user, text)
            session.persist(createdA)

            val createdB = acronymB.createExplanation(user, text)
            session.persist(createdB)

            assertEquals(2, getAllExplanations().count())

            val foundA = findExplanation(session, acronymA, text)
            val foundB = findExplanation(session, acronymB, text)

            assertEquals(createdA, foundA)
            assertEquals(createdB, foundB)

            assertNotEquals(foundA, foundB)
        }

        @Test
        fun `deleteExplanation deletes explanation`() {
            val explanation = acronymA.createExplanation(user, "Some explanation.")
            assertEquals(1, getAllExplanations().count())

            deleteExplanation(session, explanation)
            assertEquals(0, getAllExplanations().count())
        }

        @Test
        fun `deleteExplanation updates acronym`() {
            val explanation = acronymA.createExplanation(user, "Some explanation.")
            assertTrue(acronymA.explanations.contains(explanation))

            deleteExplanation(session, explanation)
            assertFalse(acronymA.explanations.contains(explanation))
            assertEquals(0, acronymA.explanations.size)
        }
    }
}
